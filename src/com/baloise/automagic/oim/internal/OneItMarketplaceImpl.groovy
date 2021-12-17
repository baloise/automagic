package com.baloise.automagic.oim.internal

import com.baloise.automagic.common.Registered
import com.baloise.automagic.oim.OneItMarketplaceService

class OneItMarketplaceImpl extends Registered implements OneItMarketplaceService {

	String MYC_URL_BASE = new String(Base64.decoder.decode('aHR0cHM6Ly9teWNsb3VkLXFhLmJhbGdyb3VwaXQuY29tL1dlYkFQSQ=='))
	String OrgEntityId =  new String(Base64.decoder.decode('T1JHLUY0OTYwQjUxLTIxQzItNENBQy05OTdDLTk3NEIxNTExMUVCNg=='))
	
	String getToken() {
		steps.httpRequest(url:  new String(Base64.decoder.decode('aHR0cHM6Ly9vaW0tYXBpLWJpemRldm9wcy1ub24tcHJvZC5hcHBzLmNhYXNkMDEuYmFsZ3JvdXBpdC5jb20vb2MvdjAuMS90ZXN0L2dldHRva2Vu'))).content
	}
	
	String myCloud(String url, String body='') {
		boolean ignoreSslErrors = true
		boolean consoleLogResponseBody = false
	
		Map params = [
				consoleLogResponseBody: consoleLogResponseBody,
				contentType: 'APPLICATION_JSON',
				customHeaders:  [
					[maskValue: true, name: 'Authorization', value: 'Bearer '+token],
					[maskValue: false, name: 'Offset', value: '-120']
				],
				httpMode: (body? 'POST' : 'GET'),
				ignoreSslErrors: ignoreSslErrors,
				url: url,
				wrapAsMultipart: false]
		if(body) params.requestBody = body
		steps.httpRequest(params).content
	}
	
	@Override
	public def getAllCIDetails(int requestNo) {
		return  steps.readJSON(text : myCloud("${MYC_URL_BASE}/V2/CI/GetAllCIDetails/OrgEntityId/${OrgEntityId}/?datafilter=RequestNo=%27${requestNo}%27"))
	}
	
	
	@Override
	public def getRequest(int requestNo) {
		steps.readJSON(text :  myCloud("${MYC_URL_BASE}/V2/Requests/${OrgEntityId}/RequestNo/${requestNo}/ItemNo/1"))
	}
	
	@Override
	public def createVM(String jsonBody) {
		steps.readJSON(text : myCloud("${MYC_URL_BASE}/GenericScripts/Execute/OrgEntityId/${OrgEntityId}/ScriptID/16",jsonBody))
	}
	
	@Override
	public def getVMDetails(String ObjectID) {
		println ObjectID
		steps.readJSON(text : myCloud("${MYC_URL_BASE}/V2/CI/GetCIMasterData/ObjectId/${ObjectID?:'NONE'}/ObjectType/VM"))
	}
}
