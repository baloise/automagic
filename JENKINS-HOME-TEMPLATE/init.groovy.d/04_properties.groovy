import java.util.logging.Logger
import jenkins.model.*

Logger log = Logger.getLogger('init.groovy.d')

def envVars = Jenkins.instance.globalNodeProperties.getAll(hudson.slaves.EnvironmentVariablesNodeProperty.class)[0]?.envVars

if (!envVars) {
    return
}

def mockConfiguration = new GroovyScriptEngine(['src', 'test/src'] as String[],this.class.getClassLoader()).with {
	loadScriptByName( 'com/baloise/automagic/mock/MockConfiguration.groovy' )
}

envVars.putAll(mockConfiguration.config.properties.collectEntries{ k,v-> [("AM_$k".toString()) : v]})
Jenkins.instance.save()

log.info "properties updated"
