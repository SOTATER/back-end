package com.sota.clone.copyopgg

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate

@SpringBootApplication
class CopyOpggApplication {

	@Bean
	fun restTemplate(builder: RestTemplateBuilder): RestTemplate {
		return builder.build()
	}

}

fun main(args: Array<String>) {
	runApplication<CopyOpggApplication>(*args)
}

