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
		steps.withCredentials(
				keys.collect{steps.string(credentialsId: "AM_${scope}_${it}", variable: it.toUpperCase())},
				action
			)
    }
}
