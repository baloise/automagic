package com.baloise.automagic.oim.internal;

import com.baloise.automagic.oim.internal.masterdata.CatalogItem
import com.baloise.automagic.oim.internal.masterdata.Environment
import com.baloise.automagic.oim.internal.masterdata.MetalCategory
import com.baloise.automagic.oim.internal.masterdata.OneItMarketplaceMasterDataService
import com.baloise.automagic.oim.internal.masterdata.SBU
import com.baloise.automagic.oim.internal.masterdata.ServerSize
import com.baloise.automagic.oim.internal.masterdata.ServiceLevel

import static com.baloise.automagic.oim.internal.masterdata.CatalogItem.*
import static groovy.json.JsonOutput.*

class MyCloudRequestBuilder {
	
	OneItMarketplaceMasterDataService oim
	
	MyCloudRequestBuilder(OneItMarketplaceMasterDataService oim) {
		this.oim = oim
	}

	
	Map<String, Map<String,String>> inflate(Map<String,String> map){
		map.collectEntries {k,v -> [(k) : [key : k , value :v]] }
	}

	String buildRequest(Map metadata, Map spec) { 
		toJson(buildRequestMap(metadata, spec)) 
	}
	
	protected Map buildRequestMap(Map metadata, Map spec) {
		String catalogItem = spec.catalogItem
		String SBUCode = metadata.SBU
		String serviceLevel = metadata.serviceLevel
		String ApplicationCode = metadata.applicationCode // not validated
		String ServerSizeCode =  spec.ServerSizeCode
		String EnvironmentCode = metadata.labels.environment
		String ADGroupsName = spec.ADGroupName // not validated
		
		CatalogItem ctlItem = CatalogItem.valueOf(catalogItem)
		
		// end parameters
		String MetalCategoryCode = oim.getMetalCategoryCode(serviceLevel)
		String changenumber = UUID.randomUUID().toString()
		
		Map<Class, String> validations = [ : ]
		validations.put(SBU, SBUCode)
		validations.put(ServiceLevel, serviceLevel)
		validations.put(ServerSize, ServerSizeCode)
		validations.put(Environment, EnvironmentCode)
		
		
		validations.each{clazz,value->
			if(!oim.isValid(clazz, value)) {
				throw new IllegalArgumentException("Invalid value '${value}' for data type '${clazz.simpleName}'")
			}
		}
		
		Map request = [
						"servicecatalogid": "6",
						"orgentityid": "ORG-F4960B51-21C2-4CAC-997C-974B15111EB6",
						"changenumber": changenumber,
						"platformcode": "VMWAR"]
		
		Map item = [
			"changenumber": changenumber,
			"SBUCode" : SBUCode,
			"ServerTypeCode" : oim.getServerTypeCode(SBUCode),
			"MetalCategoryCode" : MetalCategoryCode,
			"ApplicationCode" : ApplicationCode,
			"ServerRoleCode" : ctlItem.ServerRoleCode,
			"ServerSizeCode" : ServerSizeCode,
			"CatalogName" : ctlItem.CatalogName,
			"EnvironmentCode" : EnvironmentCode,
			"SecurityZoneCode" : oim.getSecurityZoneCode(oim.getServerTypeCode(SBUCode), EnvironmentCode),
			"ADGroupsDetails" : ADGroupsName,
			"StorageTypeCode" : oim.getStorageTypeCode(MetalCategoryCode),
			"TagDetails" : [:],
			"AdditionalDrivesDetailsInGB" : [],
		]
		
		switch(ctlItem) {
			case POSTGRESQL : 
				item.DBSize = spec.DBSize
				item.DBUserName = spec.DBUser
				item.AdditionalDrivesDetailsInGB = [[		
						"Size": "10",		
						"MountpointUser": "postgres",		
						"DriveName": "",		
						"DriveOrMountPoint": "/u01/app/postgres",		
						"MountpointGroup": "postgres",		
						"LVName": "lv1",		
						"FileSystemType": "xfs",		
						"IsMountPoint": "N"	
					],[		
						"Size": spec.DBSize*1.25 as String,		
						"MountpointUser": "postgres",		
						"DriveName": "",		
						"DriveOrMountPoint": "/u01/pgdata",		
						"MountpointGroup": "postgres",		
						"LVName": "lv2",		
						"FileSystemType": "xfs",		
						"IsMountPoint": "N"	
					],[		
						"Size": spec.DBSize*5 as String,		
						"MountpointUser": "postgres",		
						"DriveName": "",		
						"DriveOrMountPoint": "/u99/pgbackup",		
						"MountpointGroup": "postgres",		
						"LVName": "lv3",		
						"FileSystemType": "xfs",		
						"IsMountPoint": "N"	
					]]
			break
			case JBOSS : 
			
			item.hdnJbossJson = [
					"jdk_version": "11",
					"java_min_heap_size": "1300m",
					"java_max_heap_size": "1310m",
					"java_metaspacesize": "96M",
					"java_max_metaspacesize": "256M",
					"jboss_console_log_dir": "/var/log/jboss-cit",
					"jboss_home": "/opt/jboss-cit/7.3.0",
					"java_home": "/usr/lib/jvm/default-java",
					"jboss_config": "standalone.xml",
					"management_user": "Yes",
					"jboss_management_user": "jboss"
				]
			break
		}
		
		
		request.items = [inflate(item)]
		request.items[0].itemno = 1
		return request
	}
}
