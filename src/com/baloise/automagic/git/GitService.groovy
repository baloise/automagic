package com.baloise.automagic.git

import org.eclipse.jgit.transport.CredentialsProvider;

public interface GitService {

	/**
	 * @param remote default is 'origin'
	 * @param workkdir default is new File('.')
	 */
	String getUrl()
	void checkout(String url,  String branchName,  File workdir)
	void commitAllAndPush(File workdir, String message)
}
