package com.baloise.automagic.oim.internal.masterdata

public enum CatalogItem {
	JBOSS('App','JBSL03'), 
	POSTGRESQL('DB','PGSQL02')
	
	public final String ServerRoleCode
	public final String CatalogName
	
	
	
	private CatalogItem(String serverRoleCode, String catalogName) {
		ServerRoleCode = serverRoleCode
		CatalogName = catalogName
	}

}
