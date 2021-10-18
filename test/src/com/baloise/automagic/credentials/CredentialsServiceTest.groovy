package com.baloise.automagic.credentials

import com.baloise.automagic.mock.MockRegistry
import com.baloise.automagic.properties.PropertyService
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals;

public class CredentialsServiceTest {

    CredentialsService service

    @Before
    void setUp() {
        service = MockRegistry.get().getService(CredentialsService)
    }

    @Test
    void getInexistant() {
        assertEquals(null, service.getUsernamePassword("inexistant"))
    }
    @Test
    void getGIT() {
        def up = service.getUsernamePassword("GIT")
        assertEquals('sesam', new String(up.getPassword()))
        assertEquals('automagic', up.getUserName() )
    }
}