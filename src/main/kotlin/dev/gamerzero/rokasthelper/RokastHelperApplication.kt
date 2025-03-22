package dev.gamerzero.rokasthelper

import dev.gamerzero.rokasthelper.configuration.ApplicationProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@EnableConfigurationProperties(ApplicationProperties::class)
@SpringBootApplication
class RokastHelperApplication

fun main(args: Array<String>) {
	runApplication<RokastHelperApplication>(*args)
}
