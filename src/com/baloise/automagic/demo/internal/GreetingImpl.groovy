package com.baloise.automagic.demo.internal

import com.baloise.automagic.common.Registered
import com.baloise.automagic.demo.GreetingService

class GreetingImpl extends Registered implements GreetingService {
	
    String greet(String name = "Nobody") {
		if(name) name = " ${name}"
		return "¡Hola${name}!"
    }
}
