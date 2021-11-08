import jenkins.model.*
import java.util.logging.Logger
Logger log = Logger.getLogger('init.groovy.d')

def mockConfiguration = new GroovyScriptEngine(['src', 'test/src'] as String[],this.class.getClassLoader()).with {
	loadScriptByName( 'com/baloise/automagic/mock/MockConfiguration.groovy' )
}

def pc = mockConfiguration.createProxy(hudson.ProxyConfiguration)  
if(pc) {
	Jenkins.instance.proxy = pc
	Jenkins.instance.save()
	log.info "Proxy settings updated to ${pc.name}:${pc.port}"	
}
