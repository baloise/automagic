package com.baloise.automagic.credentials.internal

import com.baloise.automagic.common.Registered
import com.baloise.automagic.credentials.CredentialsService
import com.cloudbees.plugins.credentials.SystemCredentialsProvider
import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials
import com.cloudbees.plugins.credentials.domains.Domain

class CredentialsImpl extends Registered implements CredentialsService {


    @Override
    PasswordAuthentication getUsernamePassword(String credentialId) {
        Domain automagic = new Domain('automagic', null, null)
        UsernamePasswordCredentials upc = SystemCredentialsProvider.instance.store.getCredentials(automagic).find {it.id == credentialId}
        upc ? new PasswordAuthentication(upc.username, upc.password.plainText.toCharArray()) : null
    }
}
