package com.baloise.automagic.mock

import com.baloise.automagic.credentials.CredentialsService

class MockCredentialService implements CredentialsService{
    Map<String, Map<String, String>> credentials

    @Override
    <T> T withCredentials(String credentialId, List<String> keys, Closure<T> action) {
        action.delegate = new Expando(credentials[credentialId])
        action()
    }
}
