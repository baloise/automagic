package com.baloise.automagic.git.internal

import com.baloise.automagic.common.Automagic
import com.baloise.automagic.common.Registered
import com.baloise.automagic.credentials.CredentialsService
import com.baloise.automagic.git.GitService
import com.baloise.automagic.properties.PropertyService
import com.cloudbees.groovy.cps.NonCPS
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ResetCommand
import org.eclipse.jgit.lib.StoredConfig
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.RefSpec
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider

import static org.eclipse.jgit.lib.ConfigConstants.*
import java.nio.file.Files;


class GitImpl extends Registered implements GitService {

	
	private boolean isRemoteBranch(String url, String branch, CredentialsProvider cp){
		Git.lsRemoteRepository()
				.setRemote(url)
				.setCredentialsProvider(cp)
				.setHeads(true)
				.call().name.contains(branch)
	}

	
	String getAuthor(){ registry.getService(PropertyService).get('GIT_AUTHOR_NAME') }

	
	String getAuthorEmail(){registry.getService(PropertyService).get('GIT_AUTHOR_EMAIL')}

	
	@Override
	String getUrl() {
		if(steps?.scm?.getClass()?.simpleName=='GitSCM') return steps.scm.userRemoteConfigs[0].url
		throw new IllegalStateException('wrong scm:'+steps?.scm)
	}

	
	@Override
	public void checkout(final String url, final String branchName, final File workdir ) {
		//registry.withProxySelector {
			registry.getService(CredentialsService).withCredentials('GIT',['USERNAME', 'PASSWORD']) {
				final String branch = "refs/heads/" + branchName
				CredentialsProvider cp = new UsernamePasswordCredentialsProvider(steps.GIT_USERNAME,steps.GIT_PASSWORD)
				if (workdir.exists()) {
					Git git = new Git(new FileRepositoryBuilder()
							.setWorkTree(workdir)
							.build())
					git.fetch()
							.setRemote("origin")
							.setRefSpecs(new RefSpec(branch))
							.setCredentialsProvider(cp).call()
					git.checkout().setName(branch).setForce(true).call()
					git.reset().setRef(branch).setMode(ResetCommand.ResetType.HARD).call()
					git.clean().setForce(true).call()
				} else {
					workdir.mkdirs()
					if (isRemoteBranch(url, branch, cp)) {
						Git git = Git.cloneRepository()
								.setURI(url)
								.setBranchesToClone([branch])
								.setBranch(branch)
								.setDirectory(workdir)
								.setCredentialsProvider(cp)
								.call()
					} else {
						Git git = Git.init()
								.setDirectory(workdir)
								.call()
						new File(workdir, '.automagic').text = 'v' + Automagic.VERSION
						git.add().addFilepattern(".").call()

						git.commit()
								.setCommitter(author, authorEmail)
								.setMessage('initial commit').call()
						git.branchRename().setNewName(branchName).call()
						StoredConfig config = git.getRepository().getConfig()
						config.setString(CONFIG_REMOTE_SECTION, "origin", "url", url)
						config.setString(CONFIG_REMOTE_SECTION, "origin", "fetch", "+refs/heads/*:refs/remotes/origin/*");
						config.setString(CONFIG_BRANCH_SECTION, branchName, "remote", "origin");
						config.setString(CONFIG_BRANCH_SECTION, branchName, "rebase", "false");
						config.setString(CONFIG_BRANCH_SECTION, branchName, "merge", "refs/heads/$branchName");
						config.save();
						git.push().setCredentialsProvider(cp).call()
					}
			  }
			}
		//}
	}

	
	@Override
	void commitAllAndPush(File workdir, String message) {
		if(!message) throw new IllegalArgumentException("commit message must not be empty")
		if(!workdir.exists()) throw new IllegalArgumentException("$workdir not found")
		//registry.withProxySelector {
			registry.getService(CredentialsService).withCredentials('GIT',['USERNAME', 'PASSWORD']) {
				CredentialsProvider cp = new UsernamePasswordCredentialsProvider(steps.GIT_USERNAME, steps.GIT_PASSWORD)

				Git git = new Git(new FileRepositoryBuilder()
						.setWorkTree(workdir)
						.build())

				git.add().addFilepattern(".").call()

				git.commit()
						.setCommitter(author, authorEmail)
						.setMessage(message).call()

				git.push().setCredentialsProvider(cp).call()
			}
		//}
	}

	@Override
	String getCommitLink(String hash, String url) {
		try {
			switch (url[8..-5]) {
				case ~/^(bitbucket|vcs).*/:
					def (org, repo) = url[0..-5].split('/')[-2..-1]
					return "https://bitbucket.balgroupit.com/projects/${org.toUpperCase()}/repos/${repo}/commits/$hash"
				default:
					return "${url[0..-5]}/commit/$hash"
			}
		} catch(e) {
			return "https://github.com/baloise/automagic/blob/main/test/src/com/baloise/automagic/git/GitServiceTest.groovy?hash=${hash}&url=${url}#L25"
		}
	}
}