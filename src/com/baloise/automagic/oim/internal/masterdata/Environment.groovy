package com.baloise.automagic.oim.internal.masterdata

public enum Environment implements MyCloudMasterData{
    PROD,
    INT,
    DEV,
    TEST,
    ACC
	
	@Override
	public String getTableName() {'ServerEnvMaster'}

	@Override
	public String getCodeFieldName() {'ServerEnvCode'}
}
