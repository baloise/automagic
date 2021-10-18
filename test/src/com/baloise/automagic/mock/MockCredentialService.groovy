package com.baloise.automagic.mock

import com.baloise.automagic.credentials.CredentialsService

class MockCredentialService implements CredentialsService{
    Map<String, Map<String, String>> credentials

    @Override
    PasswordAuthentication getUsernamePassword(String credentialId) {
        create(credentials[credentialId])
    }

    PasswordAuthentication create(unpw){ unpw ? new PasswordAuthentication(unpw.username,unpw.password.toCharArray()): null}
}
