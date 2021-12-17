import com.baloise.automagic.common.Registry
import com.baloise.automagic.magic.MagicService

def call() {
	Registry.get(this).getService(MagicService.class).magic()
}
