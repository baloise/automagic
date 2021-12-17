package com.baloise.automagic.common

import com.cloudbees.groovy.cps.NonCPS

// yes, I know ... composition over inheritance, 
// but traits don't work in Jenkins (https://issues.jenkins.io/browse/JENKINS-46145) 
// and Mixins are deprecated (http://docs.groovy-lang.org/latest/html/api/groovy/lang/Mixin.html)
abstract class Registered implements Serializable, Constructed<Registered, Object> {
	protected Registry registry
	protected Script steps
	
	@NonCPS
	def propertyMissing(String name) { steps.getProperty(name) }
	
	@NonCPS
	def methodMissing(String name, args) {
		steps.invokeMethod(name,args)
	}
	
	def sleep(int seconds) {
		steps.sleep(seconds)
	}
	
	@Override
	public Registered construct(Object params = null) {
		return this;
	}
}
