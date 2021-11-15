import com.datapipe.jenkins.vault.configuration.GlobalVaultConfiguration
import com.datapipe.jenkins.vault.configuration.VaultConfiguration

import jenkins.model.*
import hudson.*
import hudson.model.*
import org.jenkinsci.plugins.workflow.libs.*

import com.cloudbees.plugins.credentials.CredentialsScope
import com.cloudbees.plugins.credentials.SystemCredentialsProvider
import com.cloudbees.plugins.credentials.domains.Domain
import com.datapipe.jenkins.vault.credentials.VaultTokenCredential
import hudson.util.Secret

import java.util.logging.Logger

Logger log = Logger.getLogger('init.groovy.d')

try {
	Class.forName('com.datapipe.jenkins.vault.configuration.GlobalVaultConfiguration')
	log.info 'Jenkins vault plugin detected'
} catch( e ) {
	log.warning 'Jenkins vault plugin not loaded'
	return
}


VaultConfiguration vconf = GlobalVaultConfiguration.get().configuration
String vaultHost = "127.0.0.1:8280"
if(vconf.vaultUrl.contains(vaultHost)) {
	log.warning "Current vault config points to ${vconf.vaultUrl} and not as expected to ${vaultHost}"
	log.warning 'reusing vault config and not starting server'
	return
}

vconf.vaultCredentialId = 'vaultToken'
vconf.vaultUrl = "http://${vaultHost}"

try {
	Process vault = "vault server -dev -dev-listen-address=${vaultHost}".execute()
	Runtime.getRuntime().addShutdownHook(new Thread({ vault.destroyForcibly() }))

	BufferedReader inp = new BufferedReader(new InputStreamReader(vault.in))

	String unsealKey, rootToken
	int c
	while (!rootToken && c < 50) {
		String line = inp.readLine()
		println line
		switch (line) {
			case ~/^Unseal Key.*/:
				unsealKey = line.split(": ", 2)[1]
				break
			case ~/^Root Token.*/:
				rootToken = line.split(": ", 2)[1]
				break
		}
	}
	inp.close()
	println unsealKey
	println rootToken
	if (!rootToken) {
		log.warning "VAULT_TOKEN not detected. Skipping vault."
		vconf.vaultCredentialId = null
		return
	}
	log.info "vault started at ${vconf.vaultUrl}"

	String credentialId = 'vaultToken'
	def store = SystemCredentialsProvider.instance.store
	VaultTokenCredential currentVault = store.getCredentials(Domain.global()).find { it.id == credentialId }
	VaultTokenCredential newVault = new VaultTokenCredential(CredentialsScope.GLOBAL, credentialId, 'vault credentials', Secret.fromString(rootToken))
	if (currentVault) {
		store.updateCredentials(Domain.global(), currentVault, newVault)
	} else {
		store.addCredentials(Domain.global(), newVault)
	}
	log.info 'Jenkins vault token updated'

	def mockConfiguration = new GroovyScriptEngine(['src', 'test/src'] as String[], this.class.getClassLoader()).with {
		loadScriptByName('com/baloise/automagic/mock/MockConfiguration.groovy')
	}

	File cmdFile = new File('tmpVaultCmd.bat')
	List<File> tmpFiles = [cmdFile]
	try {
			//TODO linux support: export VAULT_ADDR="${vault}" ...
		String cmd = """set VAULT_ADDR=${vconf.vaultUrl}
set VAULT_TOKEN=${rootToken}
"""
		mockConfiguration.config.credentials.each { scope, creds ->
			File tmpJson = new File("${scope}.json")
			tmpJson.text = new groovy.json.JsonBuilder(creds).toString()
			tmpFiles += tmpJson
			cmd += "vault kv put secret/${scope} @${tmpJson.name}\n"
		}
		cmdFile.text = cmd
		log.info cmd
		log.info "${cmdFile.name}".execute().text
	} catch(e){
		log.severe e.message
	}finally {
		tmpFiles.findAll {it.exists()}.each {it.delete()}
	}
	log.info"Vault started and configured @ ${vconf.vaultUrl}"
} catch (e) {
	log.warning e.message
	log.warning "Could not start vault. Falling back to jenkins credential store."
	vconf.vaultCredentialId = null
}



