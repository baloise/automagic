package com.baloise.automagic.properties;

import com.baloise.automagic.mock.MockRegistry;
import org.junit.Before
import org.junit.Test;

import static org.junit.Assert.*;

public class PropertyServiceTest {

    PropertyService ps

    @Before
    void setUp() {
        ps = MockRegistry.get().getService(PropertyService)
    }

    @Test
    void getInexistant() {
        assertEquals(null, ps.get("inexistant"))
    }
    @Test
    void getAM_GIT_AUTHOR_NAME() {
        assertEquals('automagic', ps.get("GIT_AUTHOR_NAME"))
    }
}