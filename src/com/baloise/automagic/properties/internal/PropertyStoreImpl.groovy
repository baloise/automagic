package com.baloise.automagic.properties.internal

import com.baloise.automagic.common.Registered
import com.baloise.automagic.git.GitService
import com.baloise.automagic.properties.PropertyStoreService
import com.cloudbees.groovy.cps.NonCPS
import org.yaml.snakeyaml.Yaml

import static java.util.Collections.singletonMap

class PropertyStoreImpl extends Registered implements PropertyStoreService {

    String brachName = 'automagic'
    Map<String, String> lazyProperties
    File workdir = new File("../automagic/branches/"+brachName)
    File yamlFile = new File(workdir, 'PropertyStoreService.yaml')

    
    private Map<String, String> getProps() {
        if(lazyProperties == null) {
            GitService git = registry.getService(GitService)
            git.checkout(git.url, brachName, workdir)
            lazyProperties = yamlFile.exists() ? new Yaml().load(yamlFile.text) : [:]
        }
        lazyProperties
    }

    
    @Override
    String get(String key) {
       props[key]
    }

    
    @Override
    PropertyStoreService put(String key, String value) {
        put(singletonMap(key,value))
    }

    
    @Override
    PropertyStoreService put(Map<String,String> key2value) {
        if(key2value.every {props[it.key] == it.value}) {
            return this
        }
        props.putAll(key2value)
        yamlFile.text = new Yaml().dumpAsMap(props)
        GitService git = registry.getService(GitService)
        git.commitAllAndPush(workdir, "PropertyStore updated ${key2value.keySet()}")
        return this
    }
}
