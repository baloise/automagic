import jenkins.model.*
import hudson.*
import hudson.model.*
import org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl
import com.cloudbees.plugins.credentials.CredentialsScope
import com.cloudbees.plugins.credentials.SystemCredentialsProvider
import com.cloudbees.plugins.credentials.domains.Domain

import groovy.json.JsonSlurper
import hudson.util.Secret

import java.util.logging.Logger

Logger log = Logger.getLogger('init.groovy.d')
log.info 'updating jenkins credentials'


def mockConfiguration = new GroovyScriptEngine(['src', 'test/src'] as String[],this.class.getClassLoader()).with {
	loadScriptByName( 'com/baloise/automagic/mock/MockConfiguration.groovy' )
}


def store = SystemCredentialsProvider.instance.store
def globalCreds = store.getCredentials(Domain.global())

mockConfiguration.config.credentials.each{ scope, creds ->
  creds.each{ k,v-> 
    String key = "AM_${scope}_${k}"
    println key
    def current = globalCreds.find{it.id == key}
    def credential = new StringCredentialsImpl(CredentialsScope.GLOBAL, key, "${scope}_${k}", Secret.fromString(v))
    if(current){
		store.updateCredentials(Domain.global(), current, credential)
	} else {
		store.addCredentials(Domain.global(), credential)
	}
  }
}
