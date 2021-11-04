package com.baloise.automagic.cmdb;

import com.baloise.automagic.credentials.CredentialsService;
import com.baloise.automagic.mock.MockRegistry;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CMDBServiceTest {

    private CMDBService service;

    @Before
    public void setUp() throws Exception {
        service = MockRegistry.get().getService(CMDBService)
    }

    @Test
    public void createChange() {
        assertEquals("https://int-valuemation.baloisenet.com/vmweb", service.createChange())
    }
}