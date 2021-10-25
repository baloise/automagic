import com.baloise.automagic.common.Registry
import com.baloise.automagic.demo.GreetingService
import com.baloise.automagic.properties.PropertyStoreService
/*
 / properties will be stored on the branch automagic in the current git repository
 / if you use a multibranch pipeline you should filter you branch source by name with the following regex ^((?!automagic).)*$
*/
def call(String key, String value) {
	Registry.get(this).getService(PropertyStoreService.class).put(key, value)
}
