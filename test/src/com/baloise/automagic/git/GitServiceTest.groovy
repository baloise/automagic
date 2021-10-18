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

	@Test
	void getUrl() {
		assertEquals('https://github.com/baloise/automagic.git', git.url)
	}

	def prepareRepo(File workdir){
		Git git = Git.init()
				.setDirectory(workdir)
				.call()
		Files.writeString(workdir.toPath().resolve("README"), 'Hello Unittest');
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
