package com.baloise.automagic.git

import org.eclipse.jgit.transport.CredentialsProvider;

public interface GitService {

	String getUrl()
	void checkout(String url,  String branchName,  File workdir)
	void commitAllAndPush(File workdir, String message)
	String getCommitLink(String hash, String url)
}
