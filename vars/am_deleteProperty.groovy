import com.baloise.automagic.common.Registry
import com.baloise.automagic.properties.PropertyStoreService

/*
 / properties will be stored on the branch automagic in the current git repository
 / if you use a multibranch pipeline you should filter you branch source by name with the following regex ^((?!automagic).)*$
*/
def call(String key) {
	Registry.get(this).getService(PropertyStoreService.class).delete(key)
}
