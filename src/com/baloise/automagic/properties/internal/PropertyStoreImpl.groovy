package com.baloise.automagic.properties.internal

import com.baloise.automagic.common.Registered
import com.baloise.automagic.git.GitService
import com.baloise.automagic.properties.PropertyStoreService
import org.yaml.snakeyaml.Yaml

class PropertyStoreImpl extends Registered implements PropertyStoreService {

    String brachName = 'automagic'
    Map<String, String> lazyProperties
    File workdir = new File("../automagic/branches/"+brachName)
    File yamlFile = new File(workdir, 'PropertyStoreService.yaml')

    private Map<String, String> getProps() {
        if(lazyProperties == null) {
            println "loading Properties"
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
    String put(String key, String value) {
        if(props[key] == value) {
            return value
        }
        props[key] = value
        yamlFile.text = new Yaml().dumpAsMap(props)
        GitService git = registry.getService(GitService)
        git.commitAllAndPush(workdir, "PropertyStore updated $key")
        return value
    }
}
