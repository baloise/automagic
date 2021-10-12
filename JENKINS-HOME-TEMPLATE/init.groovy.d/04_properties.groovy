import java.util.logging.Logger
import jenkins.model.*

Logger log = Logger.getLogger('init.groovy.d')

Jenkins.instance.globalNodeProperties.getAll(hudson.slaves.EnvironmentVariablesNodeProperty.class)[0].envVars.putAll([
        'AUTOMAGIC_GIT_AUTHOR_EMAIL' : 'git@baloise.com',
        'AUTOMAGIC_GIT_AUTHOR_NAME' : 'Hans'
])
Jenkins.instance.save()

log.info "properties updated"
