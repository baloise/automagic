package com.baloise.automagic.oim.internal.masterdata

public enum MetalCategory implements MyCloudMasterData {
	GP,
	G,
	S,
	B
	
	@Override
	public String getTableName() {'MetalCategoryMaster'}

	@Override
	public String getCodeFieldName() {'CategoryCode'}
}
