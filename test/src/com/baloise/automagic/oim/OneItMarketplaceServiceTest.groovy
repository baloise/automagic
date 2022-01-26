package com.baloise.automagic.oim

import com.baloise.automagic.mock.MockRegistry

import static org.assertj.core.api.Assertions.assertThat
import static org.junit.Assert.*

import org.junit.Before
import org.junit.Ignore
import org.junit.Test



class OneItMarketplaceServiceTest {
	
	OneItMarketplaceService oim

	@Before
	void setUp() {
		oim = MockRegistry.get().getService(OneItMarketplaceService)
	}

	@Test
	@Ignore
	void getAllCIDetails_266() {
		def details = oim.getAllCIDetails(266)
		assertEquals("Success", details.Status)		
		assertEquals("10.161.56.30", details.Result[0].PrimaryIP)
	}
	
	@Test
	@Ignore
	void getVMDetails_myctt62() {
		def details = oim.getVMDetails("mcl-mcdt632")
		assertEquals("Success", details.Status)		
		assertEquals(279, details.Result[0].RequestNo)
	}

}
