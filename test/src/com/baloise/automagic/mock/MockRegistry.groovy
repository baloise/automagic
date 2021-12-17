package com.baloise.automagic.mock


import com.baloise.automagic.common.Registry
import com.baloise.automagic.credentials.CredentialsService
import com.baloise.automagic.properties.PropertyService

import groovy.json.JsonSlurper
import hudson.TestProxyConfiguration
import hudson.plugins.git.GitSCM

class MockRegistry extends Registry {
  

    def lazyJenkins
    @Override
    def getJenkins() {
        if(!lazyJenkins) lazyJenkins = jenkins()
        return lazyJenkins
    }

    static Registry get(){
        Map steps = steps()
        MockRegistry registry = new MockRegistry().construct(steps)
        registry.registerService(CredentialsService, new MockCredentialService(credentials : MockConfiguration.config.credentials, steps : steps))
        registry.registerService(PropertyService, new MockPropertyService(properties : MockConfiguration.config.properties))
        registry
    }
	
	static def reMap(Map inp, Map out = new HashMap()) {
		inp.each { k,v -> out[k] = v in Map ? reMap(v) : v}
		out
	}
	
	static def reMap(List inp, List out = new ArrayList()) {
		inp.each { v -> out += (v in Map || v in List) ? reMap(v) : v}
		out
	}
	
	static def readJSON(Map json) {
		reMap(new groovy.json.JsonSlurper().parseText(json.text))
	}
	

    private static Map steps() {
        def steps = [:]
        steps.echo = { text -> println "echo: " + text }
        steps.sh = { cmd -> println "executed shell cmd: " + cmd }
        steps.ansiColor = { type, func -> func() }

        steps.git = { input -> println input }
        steps.error = { text -> println text }
        steps.withEnv = { array, func -> func() }
        steps.withCredentials = { array, func -> func() }
        steps.tool = { name -> return "/opt/maven" }
        steps.pwd = { -> return "/var/lib/jenkins/workspace/folder/job" }
        steps.scm = new GitSCM('https://github.com/baloise/automagic.git')
        steps.stage = { text, func -> println(text); func() }
        steps.wrap = { map, func -> func() }
        steps.answerInput = { input -> println "answer input" }

        steps.log = []

		steps.readJSON = MockRegistry.&readJSON
        steps.env = [:]
        steps.env.BITBUCKET_URL = "https://bitbucket.balgroupit.com"
        steps.env.CHARTMUSEUM_URL = "https://charts.shapp.os1.balgroupit.com"
        steps.env.QUAY_URL = "quay.balgroupit.com"
        steps.env.JENKINS_URL = "https://ci.balgroupit.com"
        steps.env.getProperty = { name -> return name }
        steps.currentBuild = [:]

        steps.USERNAME = "mockedUsername"
        steps.PASSWORD = "mockedPassword"


        steps.readYaml = { input ->
            steps.fileSystem.readYaml(input.file)
        }
        steps.writeFile = {
            file -> steps.fileSystem.writeFile(file)
        }

        steps.readFile = { path -> println "file read: " + path; return steps.fileSystem.readFile(path) }
        steps.fileExists = { name -> return steps.fileSystem.fileExists(name) }
        steps.sleep = { input -> steps.sleepMock.addSleep(input.time, input.unit) }
        steps.httpRequest = { 
			map -> 
			if(map.url) {
				println map
				
				HttpURLConnection con = new URL(map.url).openConnection()
				con.setRequestProperty("Content-Type", "application/json")
				map.customHeaders.each{
					con.setRequestProperty(it.name, it.value)
				}
				return [content : con.inputStream.text]
			}
			return map
		}
        steps.binding = new Binding()
        return steps
    }

    private static def jenkins() {
        def jenkinsMock = [jenkinsMock:true]
        jenkinsMock.proxy = createProxy()
        return jenkinsMock
    }


    static TestProxyConfiguration createProxy() {
		MockConfiguration.createProxy(hudson.TestProxyConfiguration)
    }

}
