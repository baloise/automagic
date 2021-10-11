# automagic

automagic is a platform to for declarative life cycle and configuration management of virtual machines at Baloise. It allows you to create, change and delete infrastructure, middleware and applications using a declarative GitOps approach. automagic uses git for source code management and Jenkins for CI/CD pipeline automation.


Please find all further information in the [user manual](https://github.com/baloise/automagic/tree/main/docs/manual).

# Use
 
To use in your pipeline add the following to the top of your Jenkinsfile

```
library identifier: 'automagic@release', retriever: modernSCM(
  [$class: 'GitSCMSource', remote: 'https://github.com/baloise/automagic.git']
)
```

then use as any inbuilt command, for example

```
greet 'World'
```

We highly recommend to configure this as [global library](https://www.jenkins.io/doc/book/pipeline/shared-libraries/#global-shared-libraries) as in the dev setup, so you the code is trusted and you can shorten the import to

```
library 'automagic@release'
```

# Contribute

It is very easy to write pipeline steps - have a look at the existing code, i.e. [greet.groovy](./vars/greet.groovy). 

Yes please take the time to write a minimal documentation ;-) 

For a deep dive please read the [official documentation](https://jenkins.io/doc/book/pipeline/shared-libraries/).  


# dev setup

You can use [jenkins-maven-plugin](https://github.com/baloise/jenkins-maven-plugin) ... 

```
mvn -Djenkins.home=/this/folder/might/grow/big/JENKINS_HOME jenkins:run
```

Enter "exit" on the console to end jenkins process.

On the first run all necessary plugins are installed - this may take some minutes. 

Head over to [http://localhost:8080/](http://localhost:8080/) and use the template jobs to get you up and running 🚀.


# architecture

automagic is mainly a wrapper for the [oneIT marketplace api server](https://github.com/baloise/oim-api).

## behind a proxy
Set `http_proxy` / `no_proxy` properties / environment variables, see [JENKINS-HOME-TEMPLATE/init.groovy.d/01_proxy.groovy](./JENKINS-HOME-TEMPLATE/init.groovy.d/01_proxy.groovy)


https://baloise.github.io/automagic/site/main/linecoverage.svg

If you need a forwarding proxy, have a look at [https://github.com/baloise/proxy](https://github.com/baloise/proxy#installation) 



[![Line Coverage](https://baloise.github.io/automagic/site/main/linecoverage.svg)](https://baloise.github.io/automagic/site/main/jacoco)
