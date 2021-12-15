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
	
	@Override
	def magic() {
		steps.echo "using automagic v"+ Automagic.VERSION
		
		if(steps.env.NODE_NAME) steps.error """Automagic is a potentially long running pipeline. 
		You must not run inside a node but are running on '${steps.env.NODE_NAME}'. 
		For declarative pipelines please use 'agent none'. 
		For scripted pipelines make sure you are not running inside a 'node(){}'."""
		
		
		createDecommissionJiraTask("test123", "b028178")
		return
		// we do the following in two loops to avoid sleeping inside the node
		Map<String, String> yamls
		steps.node(''){
			steps.checkout steps.scm
			yamls = steps.findFiles(glob: '.automagic/**/*.yaml').collectEntries{["${it.path}": steps.readYaml(file: it.path)]}
		}
		yamls.each{ name,yaml -> 
			doMagic(name, yaml)
		}
	}
	
	def doMagic(name, yaml) {
		steps.echo "applying $name" 
		String ip
				
		String ComputerName = props.get('ComputerName-'+yaml.spec.id)
		if(ComputerName) steps.echo "ComputerName is $ComputerName"
	
		def json = oim.getVMDetails(ComputerName)
		ip = json.Status == 'Success' ? json.Result[0].PrimaryIP : ''
		if(ip) steps.echo "IP is $ip"
		
		String jiraTask = props.get('JIRA-Decommission-ID-'+ComputerName)
		
		if(yaml.spec.status == "decommissioned") {
			steps.echo("Starting Decommissioning procedure")
			assert ComputerName
			if(jiraTask) {
				steps.error "JIRA issue for decommissioning already exists: " + jiraTask
			}
			steps.echo("Scheduling " + ComputerName + " for decommissioning.")
			createDecommissionJiraTask(ComputerName,yaml.metadata.serviceOwner)
			return
		}
	
		if(yaml.spec.status == "active") {
			if(jiraTask) {
				steps.error "JIRA issue for decommissioning exists: " + jiraTask
			} 
			if(!ComputerName || json.Status != 'Success'){
				 steps.echo "Creating VM"
				 String changeNo = "CR"+java.util.UUID.randomUUID()
				 steps.echo "changeNo = $changeNo"
				 String req_body = steps.libraryResource(resource:'mycloud/createVM.json').replaceAll("<changenumber>", changeNo)
				 yaml.spec.each{ key, value ->
				 req_body = req_body.replaceAll("<$key>", "$value")
				 }
				 json = oim.createVM(req_body)
				 assert json.Status == 'Success'
				 int request_no = json.Result
				 steps.echo "Request number is $request_no"
	
				 // All my dreams fulfil
				 while(true) {
					 json = oim.getRequest(request_no)
					 assert json.Status == 'Success'
					 steps.echo "RequestStatus = " + json.Result[0].RequestStatus
					 if(json.Result[0].RequestStatus == "Fulfilment Completed") break
					 steps.echo "sleeping 37 seconds"
					 steps.sleep(37)
				 }
	
				 json = oim.getAllCIDetails(request_no)
				 assert json.Status == 'Success'
				 ip = json.Result[0].PrimaryIP
				 ComputerName = json.Result[0].ComputerName
				 steps.echo "ComputerName is $ComputerName"
				 steps.echo "IP is $ip"
				 props.put('ComputerName-'+yaml.spec.id, ComputerName)
			}
			steps.node(''){
				steps.sh "ssh -o BatchMode=true -o ConnectTimeout=7 -o StrictHostKeyChecking=no $ip"
			}
			return
		}
		steps.error "invalid status: ${yaml.spec.status}"
	}

	
	String createDecommissionJiraTask(String host, String serviceOwner) {
		//TODO move this to OIM
		// perhaps add skill-layer when clarified what to use.
		if(!host || !serviceOwner) {
			steps.error "createDecommissionJiraTask: invalid arguments"
		}
	
		registry.getService(CredentialsService).withCredentials('JIRA',
			['USERNAME', 'PASSWORD']
	  ) {
		  
		  def res = steps.readJSON(text: steps.httpRequest(acceptType: 'APPLICATION_JSON', consoleLogResponseBody: true, contentType: 'APPLICATION_JSON', customHeaders: [[maskValue: true, name: 'Authorization', value: 'Basic '+Base64.encoder.encodeToString("${steps.JIRA_USERNAME}:${steps.JIRA_PASSWORD}".bytes)]], httpMode: 'POST', requestBody: """{
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
