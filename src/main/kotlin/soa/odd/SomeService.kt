package soa.odd

import org.slf4j.LoggerFactory
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.stereotype.Component
import soa.numbers.NumberData

private val logger = LoggerFactory.getLogger("soa.Lab5")

/** Service Activator: the second subscriber of oddChannel. */
@Component
class SomeService {
    @ServiceActivator(inputChannel = "oddChannel")
    fun handle(m: NumberData) {
        logger.info("  🔧 Service Activator: Received [{}] (type: {})", m.data, m.javaClass.simpleName)
    }
}
