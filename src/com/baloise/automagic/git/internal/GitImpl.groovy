package com.baloise.automagic.git.internal

import com.baloise.automagic.common.Automagic
import com.baloise.automagic.common.Registered
import com.baloise.automagic.credentials.CredentialsService
import com.baloise.automagic.git.GitService
import com.baloise.automagic.properties.PropertyService
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
	String getUrl(String remote = 'origin', File workdir = new File('.')) {
		Git git = new Git(new FileRepositoryBuilder()
				.setWorkTree(workdir)
				.build())
		StoredConfig config = git.getRepository().getConfig()
		config.getString(CONFIG_REMOTE_SECTION, remote, "url")
	}

	@Override
	public void checkout(final String url, final String branchName, final File workdir ) {
		registry.setProxySelector()
		PasswordAuthentication pwa = registry.getService(CredentialsService).getUsernamePassword('GIT')
		final String branch = "refs/heads/"+branchName
		CredentialsProvider cp = new UsernamePasswordCredentialsProvider(pwa.userName, pwa.password)
		if(workdir.exists()) {
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
			if(isRemoteBranch(url,branch,cp)) {
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
				Files.writeString(workdir.toPath().resolve(".automagic"), 'v'+ Automagic.VERSION);
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

	@Override
	void commitAllAndPush(File workdir, String message) {
		if(!message) throw new IllegalArgumentException("commit message must not be empty")
		if(!workdir.exists()) throw new IllegalArgumentException("$workdir not found")
		registry.setProxySelector()
		PasswordAuthentication pwa = registry.getService(CredentialsService).getUsernamePassword('GIT')
		CredentialsProvider cp = new UsernamePasswordCredentialsProvider(pwa.userName, pwa.password)

		Git git = new Git(new FileRepositoryBuilder()
				.setWorkTree(workdir)
				.build())

		git.add().addFilepattern(".").call()

		git.commit()
				.setCommitter(author, authorEmail)
				.setMessage(message).call()

		git.push().setCredentialsProvider(cp).call()
	}
}