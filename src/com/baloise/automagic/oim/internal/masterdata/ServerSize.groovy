package com.baloise.automagic.oim.internal.masterdata

public enum ServerSize implements MyCloudMasterData{
    S1,
    M1,
    M2,
    L1,
    L2,
    X1,
    L3,
    X2,
    X3
	
	@Override
	public String getTableName() {'ServerSizeMaster'}

	@Override
	public String getCodeFieldName() {'ServerSizeCode'}
}
