package com.baloise.automagic.credentials

import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials

import javax.annotation.CheckForNull

/*
 * The default CredentialsService reads credentials from the Jenkins credential domain named 'automagic'
 */
interface CredentialsService {
    @CheckForNull
    PasswordAuthentication getUsernamePassword(String credentialId)
}