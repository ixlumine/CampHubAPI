package com.camphub.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CampHubApiApplication

fun main(args: Array<String>) {
	runApplication<CampHubApiApplication>(*args)
}
