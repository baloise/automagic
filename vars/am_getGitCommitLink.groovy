import com.baloise.automagic.cmdb.CMDBService
import com.baloise.automagic.common.Automagic
import com.baloise.automagic.common.Registry
import com.baloise.automagic.demo.GreetingService
import com.baloise.automagic.git.GitService
import com.baloise.automagic.properties.PropertyStoreService

String call(String hash = null, String url = null){
	GitService git = Registry.get(this).getService(GitService)
	git.getCommitLink(hash ?: env.GIT_COMMIT, url ?: git.url)
}
