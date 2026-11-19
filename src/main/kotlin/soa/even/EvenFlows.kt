package soa.even

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.integrationFlow
import soa.numbers.NumberData

private val logger = LoggerFactory.getLogger("soa.Lab5")

@Configuration
class EvenFlows {
    /** Translates NumberData to EvenNumberData and handles it. */
    @Bean
    fun evenFlow(): IntegrationFlow =
        integrationFlow("evenChannel") {
            transform { m: NumberData ->
                val even = EvenNumberData(m.data)
                logger.info("  ⚙️  Even Transformer: {} → {}", m, even)
                even
            }
            handle<EvenNumberData> { even, _ ->
                logger.info("  ✅ Even Handler: Processed [{}]", even)
            }
        }
}
