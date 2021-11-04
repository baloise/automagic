package com.baloise.automagic.credentials

import com.baloise.automagic.mock.MockRegistry
import com.baloise.automagic.properties.PropertyService
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull;

public class CredentialsServiceTest {

    CredentialsService service

    @Before
    void setUp() {
        service = MockRegistry.get().getService(CredentialsService)
    }

    @Test()
    void getInexistant() {
        service.withCredentials("inexistant", ["nope"]){
            assertNull(NOPE)
        }
    }

    @Test
    void getGIT() {
        //TODO must access properties through steps
        service.withCredentials("secrets/GIT", ["nope"]){
            assertEquals('sesam', PASSWORD)
            assertEquals('automagic', USERNAME )
        }
    }
}