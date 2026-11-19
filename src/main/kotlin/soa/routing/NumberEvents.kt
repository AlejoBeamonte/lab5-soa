package soa.routing

import org.slf4j.LoggerFactory
import org.springframework.modulith.events.ApplicationModuleListener
import org.springframework.stereotype.Component
import soa.numbers.NumberData

private val logger = LoggerFactory.getLogger("soa.Lab5")

/** Listens to the numbers module and hands every event to the integration flow. */
@Component
class NumberEvents(
    private val sendNumber: SendNumber,
) {
    @ApplicationModuleListener
    fun on(event: NumberData) {
        logger.info("📨 Event received: {} → SendNumber gateway", event)
        sendNumber.sendNumber(event)
    }
}
