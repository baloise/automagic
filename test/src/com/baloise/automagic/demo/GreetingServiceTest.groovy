package com.baloise.automagic.demo

import com.baloise.automagic.mock.MockRegistry

import static org.assertj.core.api.Assertions.assertThat

import org.junit.Before
import org.junit.Test


class GreetingServiceTest {
	
	GreetingService greeting

	@Before
	void setUp() {
		greeting = MockRegistry.get().getService(GreetingService)
	}

	@Test
	void greeting_sayHello_when_name_is_world_then_greeting_is_Hello_World() {
		assertThat(greeting.greet("World")).isEqualTo("¡Hola World!")
	}
	
	@Test
	void greeting_sayHello_when_name_is_null_then_greeting_is_fall_back_to_nobody() {
		assertThat(greeting.greet()).isEqualTo("¡Hola Nobody!")
	}
	
	@Test
	void greeting_sayHello_when_name_is_empty() {
		assertThat(greeting.greet("")).isEqualTo("¡Hola!")
	}

}
