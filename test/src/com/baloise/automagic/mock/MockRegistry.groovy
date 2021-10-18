package com.baloise.automagic.mock

import com.baloise.automagic.common.Registry
import com.baloise.automagic.credentials.CredentialsService
import com.baloise.automagic.properties.PropertyService
import hudson.TestProxyConfiguration
import org.yaml.snakeyaml.Yaml

class MockRegistry extends Registry {
    private MockRegistry() {
        super(steps())
    }

    def lazyJenkins
    @Override
    def getJenkins() {
        if(!lazyJenkins) lazyJenkins = jenkins()
        return lazyJenkins
    }

    static Registry get(){
        MockRegistry registry = new MockRegistry()
        registry.registerService(CredentialsService, new MockCredentialService(credentials : config.credentials))
        registry.registerService(PropertyService, new MockPropertyService(properties : config.properties))
        registry
    }

    static Map lazyConfig
    static getConfig() {
        if (!lazyConfig) lazyConfig = loadConfig()
        lazyConfig
    }

    static Map loadConfig() {
        def yaml = new Yaml()
        String mockConfigurationFilename = 'AutomagicMock.yaml'
        File mockConfigurationFile = new File("test/resources/$mockConfigurationFilename")
        Map ret = [:]
        if(mockConfigurationFile.exists()) {
            println "loading $mockConfigurationFile.absolutePath"
            ret = yaml.load(mockConfigurationFile.text)
        }
        mockConfigurationFile = new File(System.getProperty('user.home'), mockConfigurationFilename)
        if(mockConfigurationFile.exists()) {
            def personal = yaml.load(mockConfigurationFile.text)
            if(personal.enabled == null || personal.enabled){
                println "loading $mockConfigurationFile.absolutePath"
                ret = deepMergeMaps(ret, personal)
            } else {
                println "$mockConfigurationFile.absolutePath disabled"
            }
        }
        ret
    }

    private static def steps() {
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
        steps.scm = null
        steps.stage = { text, func -> println(text); func() }
        steps.wrap = { map, func -> func() }
        steps.answerInput = { input -> println "answer input" }

        steps.log = []

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
        steps.httpRequest = { map -> return map}
        steps.binding = new Binding()
        return steps
    }

    private static def jenkins() {
        def jenkinsMock = [jenkinsMock:true]
        jenkinsMock.proxy = createProxy()
        return jenkinsMock
    }


    static TestProxyConfiguration createProxy() {
        // keep in sync with JENKINS-HOME-TEMPLATE/init.groovy.d/01_proxy.groovy
        String proxyConf = System.getProperty("https_proxy") ?: System.getProperty("http_proxy") ?: System.getenv("https_proxy") ?: System.getenv("https_proxy")
        if(proxyConf) {
            String noProxy = System.getProperty("no_proxy") ?: System.getenv("no_proxy")
            URL url = new URL(proxyConf)
            def (usr, pwd ) = (url.userInfo?:":").split(":",2)
            return new TestProxyConfiguration(url.host, url.port, usr, pwd, noProxy, "https://example.com")
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
