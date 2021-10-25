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
library 'automagic@release'

println(am_greet("el mundo"))

def autolib = library('automagic@release').com.baloise.automagic
def registry = autolib.common.Registry.get(this)
println registry.getService('demo.GreetingService').greet("that was complicated")

// use this job as playground / notice board in addition to the replay function

'''
	job.definition = new org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition(script, true)
	build = job.scheduleBuild2(2)
	log.info "$jobName created"
}

