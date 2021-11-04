import com.baloise.automagic.cmdb.CMDBService
import com.baloise.automagic.common.Automagic
import com.baloise.automagic.common.Registry
import com.baloise.automagic.demo.GreetingService
import com.baloise.automagic.properties.PropertyStoreService

String call() {
	Registry.get(this).getService(CMDBService.class).createChange()
}
