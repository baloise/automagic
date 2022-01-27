package com.baloise.automagic.oim.internal.masterdata

public enum ServiceLevel implements MyCloudMasterData{
	GOLD,
	GOLD_,
	SILVER,
	BRONZE
	
	@Override
	public String getTableName() {'MetalCategoryMaster'}

	@Override
	public String getCodeFieldName() {'CategoryName'}
}
