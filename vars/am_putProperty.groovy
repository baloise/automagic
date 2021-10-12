import com.baloise.automagic.common.Registry
import com.baloise.automagic.demo.GreetingService
import com.baloise.automagic.properties.PropertyStoreService

def call(String key, String value) {
	Registry.get(this).getService(PropertyStoreService.class).setProperty(key, value)
}
