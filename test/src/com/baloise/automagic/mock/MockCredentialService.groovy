package com.baloise.automagic.mock

import com.baloise.automagic.credentials.CredentialsService

class MockCredentialService implements CredentialsService{
    Map<String, Map<String, String>> credentials
    Map steps
    @Override
    <T> T withCredentials(String credentialId, List<String> keys, Closure<T> action) {
        Map tmp = credentials[credentialId]
        if(tmp) steps.putAll(credentials[credentialId])
        action.delegate = new Expando(tmp)
        action()
    }
}
