package com.baloise.automagic.properties.internal

import com.baloise.automagic.common.Registered
import com.baloise.automagic.properties.PropertyService
import com.cloudbees.plugins.credentials.SystemCredentialsProvider
import com.cloudbees.plugins.credentials.common.UsernamePasswordCredentials
import com.cloudbees.plugins.credentials.domains.Domain
import jenkins.model.Jenkins

class PropertyImpl extends Registered implements PropertyService {

    @Override
    String get(String key) {
        return Jenkins.instance.globalNodeProperties.getAll(hudson.slaves.EnvironmentVariablesNodeProperty.class)[0].envVars['AUTOMAGIC_'+key]
    }
}
