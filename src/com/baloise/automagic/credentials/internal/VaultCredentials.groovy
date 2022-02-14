package com.baloise.automagic.credentials.internal

import static groovy.json.JsonOutput.toJson

import com.baloise.automagic.common.Registered
import com.baloise.automagic.credentials.CredentialsService
import com.datapipe.jenkins.vault.configuration.GlobalVaultConfiguration

/**
 * Credentials are stored under secret
 */
class VaultCredentials extends Registered implements CredentialsService {
    
    @Override
    <T> T withCredentials(String scope, List<String> keys, Closure<T> action){
        final String prefix = scope.split('/')[-1]
		steps.withVault(vaultSecrets: [
                [
                        path: scope,
                        secretValues: keys.collect{key -> [envVar: "${prefix}_${key}".toUpperCase(), vaultKey: key]}
                ]
        ], action)
    }

	@Override
	public void setCredentials(String scope, Map<String, String> keyValues) {
		steps.httpRequest(customHeaders: [[maskValue: true, name: 'X-Vault-Token', value: vaultToken]], httpMode: 'POST', ignoreSslErrors:true, requestBody:toJson([data:keyValues]), url: "${vaultUrl}/v1/secret/data/${scope}")
	}
	
	
	private String getVaultToken(){	withCredentials('secrets-devops/VAULT', ['TOKEN']) { steps.VAULT_TOKEN  }}
	
	private String getVaultUrl() {GlobalVaultConfiguration.get().configuration.vaultUrl}
	
}
