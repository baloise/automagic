package com.baloise.automagic.oim.internal.masterdata

public enum SBU implements MyCloudMasterData{
	CH_BCH, 
	DE,
	LU_RED,
	LU_YELLOW,
	INFRA,
	BITS
	
	@Override
	public String getTableName() {'SBUMaster'}

	@Override
	public String getCodeFieldName() {'SBUCode'}
}
