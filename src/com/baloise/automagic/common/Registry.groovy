package com.baloise.automagic.common

import com.baloise.automagic.credentials.CredentialsService
import com.baloise.automagic.credentials.internal.CredentialsImpl
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
import jenkins.model.Jenkins

class Registry implements Serializable {

    Map<String, Registered> serviceRegistry = [:]

    protected Registry(steps) {
		registerService(GreetingService, new GreetingImpl(registry: this, steps: steps))
        registerService(GitService, new GitImpl(registry: this, steps: steps))
        registerService(CredentialsService, new CredentialsImpl(registry: this, steps: steps))
        registerService(PropertyService, new PropertyImpl(registry: this, steps: steps))
        registerService(PropertyStoreService, new PropertyStoreImpl(registry: this, steps: steps))
    }

    
    static Registry get(steps){
        Binding binding = steps.binding
        if(!binding.hasVariable(Registry.class.name))
            binding.setVariable(Registry.class.name, new Registry(steps))
        binding.getVariable(Registry.class.name)
    }

	@NonCPS
    protected <T> void registerService(Class<T> serviceClazz, T impl) {
        serviceRegistry[serviceClazz.name] =  impl
    }
	
    public <T> T withProxySelector(Closure<T> action) {
       if (jenkins.proxy) ProxySelector.default = new JenkinsProxySelector(jenkins.proxy)
	   return action()
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
}
