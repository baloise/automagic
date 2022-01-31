package com.baloise.automagic.magic.internal

import static groovy.json.JsonOutput.*
import static org.assertj.core.api.InstanceOfAssertFactories.PREDICATE
import static org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import org.yaml.snakeyaml.Yaml

import com.baloise.automagic.mock.MockCpsScript
import com.baloise.automagic.oim.internal.MyCloudRequestBuilder
import com.baloise.automagic.oim.internal.masterdata.MetalCategory
import com.baloise.automagic.oim.internal.masterdata.MyCloudMasterData
import com.baloise.automagic.oim.internal.masterdata.OneItMarketplaceMasterDataService
import com.baloise.automagic.oim.internal.masterdata.SBU

import net.sf.json.groovy.JsonSlurper


class MyCloudRequestBuilderTest {

	OneItMarketplaceMasterDataService oim = new OneItMarketplaceMasterDataService() {

		@Override
		public Object getMasterTable(String na) {
			throw new UnsupportedOperationException()
		}

		@Override
		public String getServerTypeCode(String SBUCode) {
			'BUS'
		}

		@Override
		public boolean isValid(Class keyClass, String value) {
			return Enum.valueOf(keyClass, value.toUpperCase().replaceAll('-+', '_'))
		}

		@Override
		public String getStorageTypeCode(String MetalCategoryCode) {
			return 'MetalCategoryCode';
		}

		@Override
		public String getSecurityZoneCode(String ServerTypeCode, String EnvironmentCode) {
			return 'seczoneCode';
		}

		@Override
		public String getMetalCategoryCode(String serviceLevel) {
			return serviceLevel.toUpperCase()[0];
		}
		
	}
	
	MyCloudRequestBuilder requestBuilder = new MyCloudRequestBuilder(oim)
	@Before
	void setUp() {
		requestBuilder = new MyCloudRequestBuilder(oim)
	}

	def parse(File input) {
		new MockCpsScript().reMap(new JsonSlurper().parse(input))
	}
	def parseText(String input) {
		new MockCpsScript().reMap(new JsonSlurper().parseText(input))
	}
	def json2GroovyMap(String input) {
		parseText("{${input}}").inspect()
	}

	Map<String, Map<String,String>> inflate(Map<String,String> map){
		map.collectEntries {k,v -> ["$k" : [key : k , value :v]] }
	}
	
	@Test
	void buildRequestPOSTGRES() {
		Map yaml = new Yaml().load(new File("test/resources/testspecPOSTGRES.yaml").text)
		Map request = requestBuilder.buildRequestMap(yaml.metadata, yaml.metadata.specs[0])
		println prettyPrint(toJson(request))
		Map vm = request.items[0]
		assertEquals('APPCODE', vm.ApplicationCode.value)
		assertEquals(yaml.metadata.specs[0].DBSize*2, vm.AdditionalDrivesDetailsInGB.value[0].Size as int)
		assertEquals(yaml.metadata.specs[0].DBSize*5, vm.AdditionalDrivesDetailsInGB.value[2].Size as int)
		assertTrue(vm.TagDetails.value.isEmpty())
	}
	
	@Test
	void buildRequestJBOSS() {
		Map yaml = new Yaml().load(new File("test/resources/testspecJBOSS.yaml").text)
		Map request = requestBuilder.buildRequestMap(yaml.metadata, yaml.metadata.specs[0])
		println prettyPrint(toJson(request))
		Map vm = request.items[0]
		assertEquals('JBSL03', vm.CatalogName.value)
		assertEquals('jboss', vm.hdnJbossJson.value.jboss_management_user)
		assertTrue(vm.TagDetails.value.isEmpty())
		assertTrue(vm.AdditionalDrivesDetailsInGB.value.isEmpty())
	}
		
	
}
