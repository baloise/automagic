import com.baloise.automagic.common.Registry
import com.baloise.automagic.demo.GreetingService

def call(String name = "Nobody") {
	Registry.get(this).getService(GreetingService.class).greet(name)
}
