package com.baloise.automagic.common

import com.baloise.automagic.demo.GreetingService
import com.baloise.automagic.demo.internal.GreetingImpl
import com.cloudbees.groovy.cps.NonCPS

class Registry {

    Map serviceRegistry = [:]
	
	Registry(steps) {
		registerService(GreetingService.class, new GreetingImpl(registry: this, steps: steps))
    }

    @NonCPS
    private <T> void registerService(Class<T> serviceClazz, T impl) {
        serviceRegistry[serviceClazz] =  impl
    }

    @NonCPS
    <T> T getService(Class<T> serviceClazz) {
        serviceRegistry[serviceClazz]
    }
}
