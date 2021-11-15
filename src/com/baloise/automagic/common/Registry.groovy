package com.baloise.automagic.common

import com.baloise.automagic.credentials.CredentialsService
import com.baloise.automagic.credentials.internal.JenkinsCredentials
import com.baloise.automagic.credentials.internal.VaultCredentials
import com.baloise.automagic.common.internal.JenkinsProxySelector
import com.baloise.automagic.demo.GreetingService
import com.baloise.automagic.demo.internal.GreetingImpl
import com.baloise.automagic.git.GitService
import com.baloise.automagic.git.internal.GitImpl
import com.baloise.automagic.properties.PropertyService
import com.baloise.automagic.properties.PropertyStoreService
import com.baloise.automagic.properties.internal.PropertyImpl
import com.baloise.automagic.properties.internal.PropertyStoreImpl
import com.cloudbees.groovy.cps.NonCPS
import com.datapipe.jenkins.vault.configuration.GlobalVaultConfiguration
import jenkins.model.Jenkins

class Registry implements Serializable, Constructed<Registry, Object> {

    Map<String, Registered> serviceRegistry = [:]
    
    static Registry get(steps){
        Binding binding = steps.binding
        if(!binding.hasVariable(Registry.class.name))
            binding.setVariable(Registry.class.name, new Registry().construct(steps))
        binding.getVariable(Registry.class.name)
    }

	@NonCPS
    protected <T> void registerService(Class<T> serviceClazz, T impl) {
        serviceRegistry[serviceClazz.name] =  impl
    }

    @NonCPS
    <T> T withProxySelector(Closure<T> action) {
        ProxySelector theDefault = ProxySelector.default
        try {
            if (jenkins.proxy) ProxySelector.default = new JenkinsProxySelector(jenkins.proxy)
            return action()
        } finally {
            ProxySelector.default = theDefault
        }
    }

    
    public <T> T getService(Class<T> serviceClazz) {
        serviceRegistry[serviceClazz.name]
    }

    
    def getService(String serviceClazz) {
        serviceRegistry['com.baloise.automagic.'+serviceClazz]
    }

    def getJenkins(){
        Jenkins.instanceOrNull
    }


	@Override
	Registry construct(Object steps) {
		registerService(GreetingService, new GreetingImpl(registry: this, steps: steps).construct())
		registerService(GitService, new GitImpl(registry: this, steps: steps).construct())
        try {
            if(GlobalVaultConfiguration.get().configuration.vaultCredentialId) {
                println "using vault credentials with id ${GlobalVaultConfiguration.get().configuration.vaultCredentialId}"
                registerService(CredentialsService, new VaultCredentials(registry: this, steps: steps).construct())
            } else {
                throw new Exception("no vault credential id set")
            }
        } catch(e) {
            println "using jenkins credentials"
            registerService(CredentialsService, new JenkinsCredentials(registry: this, steps: steps).construct())
        }
		registerService(PropertyService, new PropertyImpl(registry: this, steps: steps).construct())
		registerService(PropertyStoreService, new PropertyStoreImpl(registry: this, steps: steps).construct())
		return this
	}
}
