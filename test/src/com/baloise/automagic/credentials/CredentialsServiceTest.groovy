package com.baloise.automagic.credentials

import com.baloise.automagic.mock.MockRegistry
import com.baloise.automagic.properties.PropertyService
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNull

class CredentialsServiceTest {

    CredentialsService service
    def steps

    @Before
    void setUp() {
        service = MockRegistry.get().getService(CredentialsService)
        steps = service.steps
    }

    @Test()
    void getInexistant() {
        service.withCredentials("inexistant", ["nope"]){
            assertNull(steps.INEXISTANT_NOPE)
        }
    }

    @Test
    void getGIT() {
        service.withCredentials("GIT", ["PASSWORD", 'USERNAME']){
            assertEquals('sesam', steps.GIT_PASSWORD)
            assertEquals('automagic', steps.GIT_USERNAME )
        }
    }
}