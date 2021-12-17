package com.baloise.automagic.mock

import hudson.plugins.git.GitSCM

class MockCpsScript extends Script {

	@Override
	Object run() {
		throw new UnsupportedOperationException("You can not run this mock")
	}
	
	def echo = { text -> println "echo: " + text }
	def sh = { cmd -> println "executed shell cmd: " + cmd }
	def ansiColor = { type, func -> func() }
    
	def git = { input -> println input }
	def error = { text -> println text }
	def withEnv = { array, func -> func() }
	def withCredentials = { array, func -> func() }
	def tool = { name -> return "/opt/maven" }
	def pwd = { -> return "/var/lib/jenkins/workspace/folder/job" }
	def scm = new GitSCM('https://github.com/baloise/automagic.git')
	def stage = { text, func -> println(text); func() }
	def wrap = { map, func -> func() }
	def answerInput = { input -> println "answer input" }
	def log = []
	def env = [:]

	def currentBuild = [:]
	def USERNAME = "mockedUsername"
	def PASSWORD = "mockedPassword"
	def readYaml = { input ->
		steps.fileSystem.readYaml(input.file)
	}
	def writeFile = {
		file -> steps.fileSystem.writeFile(file)
	}

	def readFile = { path -> println "file read: " + path; return steps.fileSystem.readFile(path) }
	def fileExists = { name -> return steps.fileSystem.fileExists(name) }
	def sleep = { input -> steps.sleepMock.addSleep(input.time, input.unit) }
	
	
	MockCpsScript(){
		env.BITBUCKET_URL = "https://bitbucket.balgroupit.com"
		env.CHARTMUSEUM_URL = "https://charts.shapp.os1.balgroupit.com"
		env.QUAY_URL = "quay.balgroupit.com"
		env.JENKINS_URL = "https://ci.balgroupit.com"
		env.getProperty = { name -> return name }
	}	
	Map httpRequest(Map map){
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
	
	Map reMap(Map inp, Map out = new HashMap()) {
		inp.each { k,v -> out[k] = v in Map ? reMap(v) : v}
		out
	}
	
	List reMap(List inp, List out = new ArrayList()) {
		inp.each { v -> out += (v in Map || v in List) ? reMap(v) : v}
		out
	}
	
	def readJSON(Map json) {
		reMap(new groovy.json.JsonSlurper().parseText(json.text))
	}
	

}
