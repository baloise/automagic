package com.baloise.automagic.properties.internal

import com.baloise.automagic.common.Registered
import com.baloise.automagic.git.GitService
import com.baloise.automagic.properties.PropertyStoreService
import org.yaml.snakeyaml.Yaml

import static java.util.Collections.singletonMap

class PropertyStoreImpl extends Registered implements PropertyStoreService {

    String brachName = 'automagic'
    Map<String, String> lazyProperties
    
	
	File workdir
    File yamlFile	
	    
    private Map<String, String> getProps() {
        if(lazyProperties == null) {
			workdir = new File(System.getProperty("java.io.tmpdir"),steps.env.JOB_NAME+ "/branches/"+brachName)
			yamlFile = new File(workdir, 'PropertyStoreService.yaml')
            GitService git = registry.getService(GitService)
            git.checkout(git.url, brachName, workdir)
            lazyProperties = (yamlFile.exists() ? new Yaml().load(yamlFile.text) : [:]) as Map<String, String>
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
    PropertyStoreService delete(String key) {
        props.remove(key) ? storeProps("PropertyStore deleted ${key}") : this
    }

    @Override
    PropertyStoreService put(Map<String,String> key2value) {
        if(key2value.every {props[it.key] == it.value}) {
            return this
        }
        props.putAll(key2value)
        return storeProps("PropertyStore updated ${key2value.keySet()}")
    }

    private PropertyStoreImpl storeProps(String message) {
        yamlFile.text = new Yaml().dumpAsMap(props)
        GitService git = registry.getService(GitService)
        git.commitAllAndPush(workdir, message)
        return this
    }
}
