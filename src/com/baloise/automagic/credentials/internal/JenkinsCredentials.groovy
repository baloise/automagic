package com.baloise.automagic.credentials.internal

import com.baloise.automagic.common.Registered
import com.baloise.automagic.credentials.CredentialsService
import com.cloudbees.groovy.cps.NonCPS
import com.cloudbees.plugins.credentials.SystemCredentialsProvider
import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials
import com.cloudbees.plugins.credentials.domains.Domain


/**
 * credentials are stored with prefix AM_
 */
class JenkinsCredentials extends Registered implements CredentialsService {

    @Override
    <T> T withCredentials(String scope, List<String> keys, Closure<T> action){
    	throw new UnsupportedOperationException("need to fix this to support path in scope")
		steps.withCredentials(
				keys.collect{key-> steps.string(credentialsId: "AM_${scope}_${key}", variable: "${scope}_${key}".toUpperCase())},
				action
		)
    }

	@Override
	public void setCredentials(String scope, Map<String, String> keyValues) {
		throw new UnsupportedOperationException("Not yet implemented")
	}
}
