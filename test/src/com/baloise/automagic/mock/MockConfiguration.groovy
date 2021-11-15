package com.baloise.automagic.mock


import groovy.json.JsonSlurper

class MockConfiguration {
  
    static Map lazyConfig
    static getConfig() {
        if (!lazyConfig) lazyConfig = loadConfig()
        lazyConfig
    }
	
    static Map loadConfig() {
		String mockConfigurationFilename = 'AutomagicMock.json'
		File mockConfigurationFile = new File("test/resources/$mockConfigurationFilename")
		Map ret = [:]
		if(mockConfigurationFile.exists()) {
			println "loading $mockConfigurationFile.absolutePath"
			ret = new JsonSlurper().parseText(mockConfigurationFile.text)
		}
		mockConfigurationFile = new File(System.getProperty('user.home'), mockConfigurationFilename)
		if(mockConfigurationFile.exists()) {
			def personal = new JsonSlurper().parseText(mockConfigurationFile.text)
			if(personal.enabled == null || personal.enabled){
				println "loading $mockConfigurationFile.absolutePath"
				ret = deepMergeMaps(ret, personal)
			} else {
				println "$mockConfigurationFile.absolutePath disabled"
			}
		} else {
			println "$mockConfigurationFile.absolutePath does not exist"
		}
		ret
	}

 
    static createProxy(Class proxyConfigClass) {
        String proxyConf = System.getProperty("https_proxy") ?: System.getProperty("http_proxy") ?: System.getenv("https_proxy") ?: System.getenv("https_proxy")
        if(proxyConf) {
            String noProxy = System.getProperty("no_proxy") ?: System.getenv("no_proxy")
            URL url = new URL(proxyConf)
            def (usr, pwd ) = (url.userInfo?:":").split(":",2)
			proxyConfigClass
			return proxyConfigClass.newInstance(url.host, url.port, usr, pwd, noProxy, "https://example.com")
        }
        null
    }

    static def Map deepMergeMaps(Map lhs, Map rhs) {
        rhs.each { k, v ->
            lhs[k] = (lhs[k] in Map ? deepMergeMaps(lhs[k], v) : v)
        }
        return lhs
    }


}
