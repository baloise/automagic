import java.util.logging.Logger
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import jenkins.model.*

Logger log = Logger.getLogger('init.groovy.d')

String jobName = 'Pipemania'
if(Jenkins.instance.getItem(jobName)) {
	log.info "$jobName already exists"
} else {
	WorkflowJob job = Jenkins.instance.createProject(org.jenkinsci.plugins.workflow.job.WorkflowJob, jobName)
	String script = '''
// use library source from git
//library 'automagic-git@release'

// use library source from disk 
def autolib = library('automagic@release').com.baloise.automagic

// Directly call the class/method.
def registry = autolib.common.Registry.get(this)
println registry.getService('demo.GreetingService').greet("that was complicated")

// ..or use the wrapped var of the greeting service
println(am_greet("el mundo"))

// Don't try it the other way around at the moment since the singleton get of registry instance will fail

// use this job as playground / notice board in addition to the replay function

'''
	job.definition = new org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition(script, true)
	build = job.scheduleBuild2(2)
	log.info "$jobName created"
}

