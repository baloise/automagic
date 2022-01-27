package com.baloise.automagic.oim

import static groovy.json.JsonOutput.*
import static org.junit.Assert.*

import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.yaml.snakeyaml.Yaml

import com.baloise.automagic.mock.MockRegistry
import com.baloise.automagic.oim.internal.OneItMarketplaceImpl
import com.baloise.automagic.oim.internal.masterdata.SBU


@Ignore
class OneItMarketplaceServiceRdTest {
	
	OneItMarketplaceImpl oim

	@Before
	void setUp() {
		oim = MockRegistry.get().getService(OneItMarketplaceService)
	}

	@Test
	void getAllCIDetails_266() {
		def details = oim.getAllCIDetails(266)
		assertEquals("Success", details.Status)		
		assertEquals("10.161.56.30", details.Result[0].PrimaryIP)
	}
	
	@Test
	void getVMDetails_myctt62() {
		def details = oim.getVMDetails("mcl-mcdt632")
		assertEquals("Success", details.Status)		
		assertEquals(279, details.Result[0].RequestNo)
	}
	
	@Test
	void CHisAValisSBUCode() {
		assertTrue(oim.isValid(SBU, 'CH-BCH'))
	}
	
	@Test
	void getMasterTable() {
		println  prettyPrint(toJson(
			oim.getMasterTable('BaloiseVMWAREStorageType').Result.CustomTableRecords
			))
	}
	
	@Test
	void SecurityZoneCode() {
		assertEquals("NPROD", oim.getSecurityZoneCode("BUS","Test"))
	}
	
	@Test
	void getServerTypeCode() {
		assertEquals("BUS", oim.getServerTypeCode("BE"))
	}
	
	@Test
	void getStorageTypeCode() {
		assertEquals("HPNM", oim.getStorageTypeCode("B"))
	}
	
	
	@Test
	void getMetalCategoryCode() {
		assertEquals("B", oim.getMetalCategoryCode("Bronze"))
	}
	
	@Test
	void oimBuildRequestPostgres() {
		Map yaml = new Yaml().load(new File("test/resources/testspecPOSTGRES.yaml").text)
		println prettyPrint(oim.buildRequest(yaml.metadata, yaml.metadata.specs[0]))
	}
	
	@Test
	void oimBuildRequestJboss() {
		Map yaml = new Yaml().load(new File("test/resources/testspecJBOSS.yaml").text)
		println prettyPrint(oim.buildRequest(yaml.metadata, yaml.metadata.specs[0]))
	}

}
