package com.baloise.automagic.common.internal

import com.cloudbees.groovy.cps.NonCPS
import hudson.ProxyConfiguration

class JenkinsProxySelector extends ProxySelector implements Serializable{

    final proxyConfiguration

    //JenkinsProxySelector(ProxyConfiguration proxyConfiguration){
    JenkinsProxySelector(proxyConfiguration){
        this.proxyConfiguration = proxyConfiguration
    }

    @Override
    
    List<java.net.Proxy> select(URI uri) {
        return [proxyConfiguration.createProxy(uri.getHost())]
    }

    @Override
    
    void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        println 'connectFailed: '+ioe?.message
    }
}
