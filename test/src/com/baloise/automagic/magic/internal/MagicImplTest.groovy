package com.baloise.automagic.magic.internal

import com.baloise.automagic.common.Automagic
import com.baloise.automagic.mock.MockRegistry
import org.eclipse.jgit.api.Git
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

import java.nio.file.Files

import static org.junit.Assert.*



class MagicImplTest {
	
	MagicImpl magic
	
	@Before
	void setUp() {
		magic = new MagicImpl()
	}

	@Test
	void getCatalogNameOK() {
		assertEquals(
			'VL01',
			magic.getCatalogName('RHEL'))
		assertEquals(
				'JBSL03',
				magic.getCatalogName('JBOSS'))
	}

	
	@Test(expected = IllegalArgumentException)
	void getCatalogNameKO() {
		magic.getCatalogName('')
	}
	
	

}
