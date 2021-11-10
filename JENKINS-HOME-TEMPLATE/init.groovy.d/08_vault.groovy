////import jenkins.model.*
////import hudson.*
////import hudson.model.*
////import org.jenkinsci.plugins.workflow.libs.*
////import hudson.plugins.filesystem_scm.*
////import hudson.scm.SCM
//
//import java.util.logging.Logger
//
//Logger log = Logger.getLogger('init.groovy.d')
//log.info new Hello().greet('world')
//return
//try {
//	log.info 'vault --version'.execute().text
//} catch(e) {
//	log.severe e.message
//	return
//}
//String text = 'vault server -dev'.execute().text
//log.info text

//
//def vault = "vault server -dev".execute()
//BufferedReader inp = new BufferedReader(new InputStreamReader(vault.in))
//
//String unsealKey, rootToken
//while(!rootToken) {
//	String line = inp.readLine()
//	println line
//	switch(line) {
//		case  ~/^Unseal Key.*/ :
//			unsealKey = line.split(": ",2)[1]
//			break
//		case  ~/^Root Token.*/ :
//			rootToken = line.split(": ",2)[1]
//			break
//	}
//}
//println  unsealKey
//println  rootToken
//Runtime.getRuntime().addShutdownHook(new Thread( { vault.destroyForcibly()}  ))
