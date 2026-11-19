package soa.routing

import org.springframework.integration.annotation.Gateway
import org.springframework.integration.annotation.MessagingGateway
import soa.numbers.NumberData

/** Messaging Gateway: the entry point of the integration flow. */
@MessagingGateway
interface SendNumber {
    @Gateway(requestChannel = "numberChannel")
    fun sendNumber(message: NumberData)
}
