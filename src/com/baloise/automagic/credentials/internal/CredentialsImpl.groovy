package com.baloise.automagic.credentials.internal

import com.baloise.automagic.common.Registered
import com.baloise.automagic.credentials.CredentialsService
import com.cloudbees.groovy.cps.NonCPS
import com.cloudbees.plugins.credentials.SystemCredentialsProvider
import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials
import com.cloudbees.plugins.credentials.domains.Domain

class CredentialsImpl extends Registered implements CredentialsService {


    @NonCPS
    @Override
    <T> T withCredentials(String credentialId, List<String> keys, Closure<T> action){
        steps.withVault(vaultSecrets: [
                [
                        path: credentialId,
                        secretValues: keys.collect{[envVar: it.toUpperCase(), vaultKey: it]}
                ]
        ], action)
    }
}
