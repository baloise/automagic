package com.baloise.automagic.git

import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.junit.Ignore
import org.junit.Before
import org.junit.Test

import com.baloise.automagic.JenkinsMock


class GitServiceTest {
	
	GitService git

	@Before
	void setUp() {
		git = JenkinsMock.registry.getService(GitService.class)
	}

	@Test
	void checkout() {
		CredentialsProvider cp = new UsernamePasswordCredentialsProvider("b028178", "Werneristmeinvater.1")
		String url = "https://git.balgroupit.com/B028178/test.git"
		File dir = new File("../ob/automagic")
		git.checkout(url, "automagic", dir,cp)
	}

}
