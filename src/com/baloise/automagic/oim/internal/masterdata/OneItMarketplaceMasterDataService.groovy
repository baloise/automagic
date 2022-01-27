package com.baloise.automagic.oim.internal.masterdata

import org.eclipse.jgit.transport.CredentialsProvider;

interface OneItMarketplaceMasterDataService {
	def getMasterTable(String tableName)
	String getServerTypeCode(String SBUCode)
	String getStorageTypeCode(String MetalCategoryCode)
	String getSecurityZoneCode(String ServerTypeCode, String EnvironmentCode )       
	String getMetalCategoryCode(String serviceLevel)
	boolean isValid(Class keyClass, String value)
}
