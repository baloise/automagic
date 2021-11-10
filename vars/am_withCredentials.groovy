import com.baloise.automagic.common.Registry
import com.baloise.automagic.credentials.CredentialsService

public <T> T call(String scope, List<String> keys, Closure<T> action) {
	Registry.get(this).getService(CredentialsService).withCredentials(scope, keys,action)
}
