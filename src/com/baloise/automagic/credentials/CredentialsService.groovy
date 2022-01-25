package com.baloise.automagic.credentials


import javax.annotation.CheckForNull

/*
 * The default CredentialsService reads credentials from hashicorp vault if available, otherwise from Jenkins credentials
 */
interface CredentialsService {
    @CheckForNull
    <T> T withCredentials(String scope, List<String> keys, Closure<T> action)
    void setCredentials(String scope, Map<String, String> keyValues)
}