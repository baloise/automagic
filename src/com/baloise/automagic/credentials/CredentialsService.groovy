package com.baloise.automagic.credentials

import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials

import javax.annotation.CheckForNull

/*
 * The default CredentialsService reads credentials from hashicorp vault
 */
interface CredentialsService {
    @CheckForNull
    <T> T withCredentials(String credentialId, List<String> keys, Closure<T> action)
}