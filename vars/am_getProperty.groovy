import com.baloise.automagic.common.Registry
import com.baloise.automagic.properties.PropertyStoreService

def call(String key) {
	Registry.get(this).getService(PropertyStoreService.class).get(key)
}
