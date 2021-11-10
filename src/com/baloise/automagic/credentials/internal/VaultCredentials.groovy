package com.baloise.automagic.credentials.internal

import com.baloise.automagic.common.Registered
import com.baloise.automagic.credentials.CredentialsService
import com.cloudbees.groovy.cps.NonCPS
import com.cloudbees.plugins.credentials.SystemCredentialsProvider
import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials
import com.cloudbees.plugins.credentials.domains.Domain

/**
 * Credentials are stored under secrets
 */
class VaultCredentials extends Registered implements CredentialsService {


    
    @Override
    <T> T withCredentials(String scope, List<String> keys, Closure<T> action){
        steps.withVault(vaultSecrets: [
                [
                        path: "secrets/$scope",
                        secretValues: keys.collect{[envVar: it.toUpperCase(), vaultKey: it]}
                ]
        ], action)
    }
}
