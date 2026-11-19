package soa

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.integration.config.EnableIntegration
import org.springframework.scheduling.annotation.EnableScheduling

/**
 * Lab 5: each direct sub-package of `soa` is a Spring Modulith application module.
 * - numbers: publishes NumberData application events.
 * - routing: turns those events into messages and routes them by parity.
 * - even, odd: translate and handle the routed messages.
 */
@SpringBootApplication
@EnableIntegration
@EnableScheduling
class IntegrationApplication

fun main(args: Array<String>) {
    runApplication<IntegrationApplication>(*args)
}
