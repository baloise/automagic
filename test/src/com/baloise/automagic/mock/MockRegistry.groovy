package com.baloise.automagic.mock


import com.baloise.automagic.common.Registry
import com.baloise.automagic.credentials.CredentialsService
import com.baloise.automagic.properties.PropertyService

import hudson.TestProxyConfiguration
import hudson.plugins.git.GitSCM

class MockRegistry extends Registry {
  

    def lazyJenkins
    @Override
    def getJenkins() {
        if(!lazyJenkins) lazyJenkins = jenkins()
        return lazyJenkins
    }

    static Registry get(){
        Script steps = new MockCpsScript()
        MockRegistry registry = new MockRegistry().construct(steps)
        registry.registerService(CredentialsService, new MockCredentialService(credentials : MockConfiguration.config.credentials, steps : steps))
        registry.registerService(PropertyService, new MockPropertyService(properties : MockConfiguration.config.properties))
        registry
    }

    private static def jenkins() {
        def jenkinsMock = [jenkinsMock:true]
        jenkinsMock.proxy = createProxy()
        return jenkinsMock
    }


    static TestProxyConfiguration createProxy() {
		MockConfiguration.createProxy(hudson.TestProxyConfiguration)
    }

}
