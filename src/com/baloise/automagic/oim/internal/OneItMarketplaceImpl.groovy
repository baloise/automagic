package com.baloise.automagic.oim.internal

import java.lang.invoke.SwitchPoint
import java.util.function.Predicate

import com.baloise.automagic.common.Registered
import com.baloise.automagic.credentials.CredentialsService
import com.baloise.automagic.oim.OneItMarketplaceService
import com.baloise.automagic.oim.internal.masterdata.MyCloudMasterData

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
	public def createVM(Map metadata, Map spec) {
		steps.readJSON(text : myCloud("${MYC_URL_BASE}/GenericScripts/Execute/OrgEntityId/${OrgEntityId}/ScriptID/16",buildRequest(metadata, spec)))
	}
	
	String buildRequest(Map metadata, Map spec) {new MyCloudRequestBuilder(this).buildRequest(metadata, spec)}
	
	@Override
	public def getVMDetails(String ObjectID) {
		steps.readJSON(text : myCloud("${MYC_URL_BASE}/V2/CI/GetCIMasterData/ObjectId/${ObjectID?:'NONE'}/ObjectType/VM"))
	}
	
	@Override
	public boolean isValid(Class keyClass, String value) {
		MyCloudMasterData enumValue = EnumSet.allOf(keyClass).iterator().next()
		getMasterTable(enumValue.tableName).Result.CustomTableRecords[enumValue.codeFieldName].contains(value)
	}
	
	private transient Map<String, Object> masterTableCache = [:]
	
	@Override
	public Object getMasterTable(String tableName) {
		masterTableCache.computeIfAbsent(
			"${MYC_URL_BASE}/V2/CustomTable/GetCustomTableRecordsByName/${tableName}/${OrgEntityId}/1/999?filter=IsActive=%27Y%27", 
			{url -> steps.readJSON(text : myCloud(url))}
		)
	}
	
	private String getMapping(String tableName, Map<String, String> filter, String fieldName) {
		getMapping(tableName, {it-> filter.every{k,v->it."${k}" == v}}, fieldName)
	}
	
	private String getMapping(String tableName, Predicate p , String fieldName) {
		getMasterTable(tableName).Result.CustomTableRecords.find{p.test(it)}."${fieldName}"
	}

	@Override
	public String getServerTypeCode(String SBUCode) {
		getMapping('SBUServerTypeMapping', [SBUCode:SBUCode], 'ServerTypeCode')
	}
	
	@Override
	public String getSecurityZoneCode(String ServerTypeCode, String EnvironmentCode) {
		getMapping('BaloiseSecurityZone', 
			{it.ServerTypeCode == ServerTypeCode && it.EnvironmentCode.split('/').contains(EnvironmentCode.toUpperCase())}
			, 'SecurityZoneCode')
	}
	
	@Override
	public String getStorageTypeCode(String MetalCategoryCode) {
		getMapping('BaloiseVMWAREStorageType', {it.MetalCategoryCode.split('/').contains(MetalCategoryCode)}, 'StorageCode')
	}
	
	@Override
	public String getMetalCategoryCode(String serviceLevel) {
		getMapping('MetalCategoryMaster', [CategoryName:serviceLevel], 'CategoryCode')
	}
	
	@Override
	public String decodePassword(String encodedPassword) {
		registry.getService(CredentialsService).withCredentials('JBOSS_MANAGEMENT_PRESHARED_KEY',['KEY']) {
			return sh( returnStdout: true, script: "echo ${encodedPassword} | openssl enc -aes-256-cbc -md sha512 -pbkdf2 -salt -a -d -pass pass:${steps.JBOSS_MANAGEMENT_PRESHARED_KEY_KEY}").trim()
		}
	}


}
