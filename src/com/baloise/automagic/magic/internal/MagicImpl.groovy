package com.baloise.automagic.magic.internal

import com.baloise.automagic.common.Automagic
import com.baloise.automagic.common.Registered
import com.baloise.automagic.credentials.CredentialsService
import com.baloise.automagic.magic.MagicService
import com.baloise.automagic.oim.OneItMarketplaceService
import com.baloise.automagic.properties.PropertyStoreService

class MagicImpl extends Registered implements MagicService {

	
	PropertyStoreService getProps() { registry.getService(PropertyStoreService) }
	OneItMarketplaceService getOim() { registry.getService(OneItMarketplaceService)}
	CredentialsService getCreds() { registry.getService(CredentialsService)}
	
	@Override
	def magic() {
		echo "using automagic v"+ Automagic.VERSION
		
		if(env.NODE_NAME) error """Automagic is a potentially long running pipeline. 
		You must not run inside a node but are running on '${env.NODE_NAME}'. 
		For declarative pipelines please use 'agent none'. 
		For scripted pipelines make sure you are not running inside a 'node(){}'."""
		
		// we do the following in two loops to avoid sleeping inside the node
		Map<String, String> yamls
		node(''){
			checkout scm
			yamls = findFiles(glob: '.automagic/**/*.yaml').collectEntries{["${it.path}": readYaml(file: it.path)]}
		}
		yamls.each{ name,yaml ->
			yamls.metadata.specs.each{spec -> 
				doMagic(name, yaml, spec)
			}
		}
	}
	
	String getCatalogName(kind) {
		switch (kind) {
			case 'RHEL' : return 'VL01'
			case 'JBOSS' : return 'JBSL03'
			default: throw new IllegalArgumentException("automagic is not aware of a catalog for "+kind)  
		}
	}
	
	def doMagic(name, yaml, spec) {
		echo "applying $name" 
		String ip
				
		String ComputerName = props.get('ComputerName-'+spec.id)
		if(ComputerName) echo "ComputerName is $ComputerName"
	
		def json = oim.getVMDetails(ComputerName)
		ip = json.Status == 'Success' ? json.Result[0].PrimaryIP : ''
		if(ip) echo "IP is $ip"
		
		String jiraTask = props.get('JIRA-Decommission-ID-'+ComputerName)
		
		if(spec.status == "decommissioned") {
			echo("Starting Decommissioning procedure")
			assert ComputerName
			if(jiraTask) {
				echo "JIRA issue for decommissioning already exists: " + jiraTask
				return
			}
			echo("Scheduling " + ComputerName + " for decommissioning.")
			createDecommissionJiraTask(ComputerName,yaml.metadata.serviceOwner)
			return
		}
	
		if(spec.status == "active") {
			if(jiraTask) {
				error "JIRA issue for decommissioning exists: " + jiraTask
			} 
			if(!ComputerName || json.Status != 'Success'){
				 echo "Creating VM"
				 String changeNo = "CR"+java.util.UUID.randomUUID()
				 echo "changeNo = $changeNo"
				 
				 // TODO merge the jsons and use proper templating anb logic , i.e. calculate DB drive sizes
				 String jsonTemplateName = spec.catalogItem == 'POSTGRESQL' ? 'postgresql.json' : 'createVM.json'
				 String req_body = libraryResource(resource:'mycloud/createVM.json').replaceAll("<changenumber>", changeNo)
				 spec.each{ key, value ->
					 req_body = req_body.replaceAll("<$key>", "$value")
				 }
				 req_body = req_body.replaceAll("<CatalogName>", getCatalogName(spec.catalogItem)) 
				 json = oim.createVM(req_body)
				 assert json.Status == 'Success'
				 int request_no = json.Result as int
				 echo "Request number is $request_no"
	
				 // All my dreams fulfil
				 while(true) {
					 json = oim.getRequest(request_no)
					 assert json.Status == 'Success'
					 echo "RequestStatus = " + json.Result[0].RequestStatus
					 if(json.Result[0].RequestStatus == "Fulfilment Completed") break
					 echo "sleeping 37 seconds"
					 sleep(37)
				 }
	
				 json = oim.getAllCIDetails(request_no)
				 assert json.Status == 'Success'
				 ip = json.Result[0].PrimaryIP
				 ComputerName = json.Result[0].ComputerName
				 echo "ComputerName is $ComputerName"
				 echo "IP is $ip"
				 String encodedPassword = json.Result[0].CustomField11
				 echo "decoding and storing ${encodedPassword}" 
				 node(''){
					 creds.setCredentials("JBOSS", ["${ComputerName.toUpperCase()}": oim.decodePassword(encodedPassword)])
				 }
				 props.put('ComputerName-'+spec.id, ComputerName)
				 if(spec.catalogItem == 'POSTGRESQL') {
					 String dbName = json.Result[0].CustomField12 +json.Result[0].CustomField13
					 props.put('DataBaseName-'+spec.id, dbName)
				 }
			}
			node(''){
				
				sh "ssh -o BatchMode=true -o ConnectTimeout=7 -o StrictHostKeyChecking=no $ip"
			}
			return
		}
		error "invalid status: ${spec.status}"
	}

	
	String createDecommissionJiraTask(String host, String serviceOwner) {
		//TODO move this to OIM
		// perhaps add skill-layer when clarified what to use.
		if(!host || !serviceOwner) {
			error "createDecommissionJiraTask: invalid arguments"
		}
	
		creds.withCredentials('JIRA',
			['USERNAME', 'PASSWORD']
	  ) {
		  
		  def res = readJSON(text: httpRequest(acceptType: 'APPLICATION_JSON', consoleLogResponseBody: true, contentType: 'APPLICATION_JSON', customHeaders: [[maskValue: true, name: 'Authorization', value: 'Basic '+Base64.encoder.encodeToString("${JIRA_USERNAME}:${JIRA_PASSWORD}".bytes)]], httpMode: 'POST', requestBody: """{
     "fields": {
     "project": {
       "id": "56280"
     },
     "summary": "Server Decommission of host ${host}",
     "reporter": {
       "name": "${serviceOwner}"
     },
     "issuetype": {
       "id": "3"
     },
     "labels": [
     "automagic",
     "hcl"
     ],
     "description": "Please shutdown and decommission the host: ${host}",
     "customfield_24350": {
       "id": "52620"
     },
     "customfield_27053": "Datacenter / Infrastructure"
     }
     }""", url: new String(Base64.decoder.decode('aHR0cHM6Ly9qaXJhLmJhbG9pc2VuZXQuY29tL2F0bGFzc2lhbi9yZXN0L2FwaS8yL2lzc3VlLw==')), wrapAsMultipart: false).content)
				  props.put('JIRA-Decommission-ID-'+host, res.key)
		  
	  }
	}
}
