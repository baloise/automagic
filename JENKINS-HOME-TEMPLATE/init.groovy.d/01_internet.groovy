import hudson.ProxyConfiguration
import jenkins.model.*
import java.util.logging.Logger
Logger log = Logger.getLogger('init.groovy.d')

def mockConfiguration = new GroovyScriptEngine(['src', 'test/src'] as String[],this.class.getClassLoader()).with {
	loadScriptByName( 'com/baloise/automagic/mock/MockConfiguration.groovy' )
}

def pc = mockConfiguration.createProxy(ProxyConfiguration)
if(pc) {
	Jenkins.instance.proxy = pc
	Jenkins.instance.save()
	log.info "Proxy settings updated to ${pc.name}:${pc.port}"	
}


try {
	URL url = new URL('https://example.com')
	log.info "Connection to $url ..."
	HttpURLConnection con = ProxyConfiguration.open(url) as HttpURLConnection
	int rspc = con.responseCode
	log.info "... returned status code $rspc"
	if(rspc >=400) {
		throw new IOException("Bad response code $rspc")	
	}
} catch (e) {
	 log.severe e.message
	 log.severe "no internet connection, do you need a proxy?"
	 log.severe "exiting"
	 System.exit 666
}