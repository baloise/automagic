package com.baloise.automagic.git

import com.baloise.automagic.common.Automagic
import com.baloise.automagic.mock.MockRegistry
import org.eclipse.jgit.api.Git
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

import java.nio.file.Files

import static org.junit.Assert.*



class GitServiceTest {
	
	GitService git

	@Before
	void setUp() {
		git = MockRegistry.get().getService(GitService)
	}

	// If you are viewing this in your browser, you might have found a bug in automagic.
	// If you are a developer you can test & implement below otherwise please file a bug under https://github.com/baloise/automagic/issues
	// You find should see the url and hash variables in you browsers address bar.
	// Thanks for you contribution :-)

	@Test
	void getLinkFallback() {
		assertEquals(
			'https://github.com/baloise/automagic/blob/main/test/src/com/baloise/automagic/git/GitServiceTest.groovy?hash=h13&url=null#L25',
			git.getCommitLink('h13',null))
	}

	@Test
	void getLinkBitBucket() {
		assertEquals(
			'https://bitbucket.balgroupit.com/projects/EINZELLEBEN/repos/server/commits/2d4f7f31ade6859dd0925cb068f9f627befa8d98',
			git.getCommitLink('2d4f7f31ade6859dd0925cb068f9f627befa8d98','https://bitbucket.balgroupit.com/scm/einzelleben/server.git'))
	}

	@Test
	void getLinkVCS() {
		assertEquals(
			'https://bitbucket.balgroupit.com/projects/DIVAPPL/repos/KLMahnen/commits/eb6bd2471a908224a66549a9285faa027bd6fd4a',
			git.getCommitLink('eb6bd2471a908224a66549a9285faa027bd6fd4a','https://vcs.balgroupit.com/git/divappl/KLMahnen.git'))
	}

	@Test
	void getLinkGithub() {
		assertEquals(
			'https://github.com/baloise/automagic/commit/10779f194e2f5141157c7bc11c056dc78739ab42',
			git.getCommitLink('10779f194e2f5141157c7bc11c056dc78739ab42','https://github.com/baloise/automagic.git'))
	}

	@Test
	void getLinkGitea() {
		assertEquals(
			'https://git.balgroupit.com/ITCH-playground/CashCalculator/commit/bb300a36b43f6992d7d9aa51895d1bef5a7450a3',
			git.getCommitLink('bb300a36b43f6992d7d9aa51895d1bef5a7450a3','https://git.balgroupit.com/ITCH-playground/CashCalculator.git'))
	}


	@Test
	void getUrl() {
		assertEquals('https://github.com/baloise/automagic.git', git.url)
	}

	def prepareRepo(File workdir){
		Git git = Git.init()
				.setDirectory(workdir)
				.call()
		
		new File(workdir,"README").text =  'Hello Unittest'
		git.add().addFilepattern(".").call()

		git.commit()
				.setCommitter('junit', 'junit@example.com')
				.setMessage('initial commit').call()
	}

	@Test
	void roundTrip() {
		File tmpDir = File.createTempDir()
		File remote = new File(tmpDir , 'remote')
		File workdir = new File(tmpDir , 'work')
		File txtFile = new File(workdir,"automagic.txt")
		try {
			prepareRepo(remote)
			String url = remote.toURI().toString().replaceFirst('file:', 'file://')
			workdir.mkdirs()
			git.checkout(url, "automagic", workdir)
			assertFalse(txtFile.exists())
			txtFile.text = 'Hello World ' + new Date()
			git.commitAllAndPush(workdir, 'Hello world')
			git.checkout(url, "automagic", workdir)
			assertTrue(txtFile.exists())
			workdir.deleteDir()
			assertFalse(txtFile.exists())
			git.checkout(url, "automagic", workdir)
			assertTrue(txtFile.exists())
		} finally {
			tmpDir.deleteDir()
		}
	}

}
