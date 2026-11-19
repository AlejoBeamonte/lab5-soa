package soa.odd

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.dsl.integrationFlow
import soa.numbers.NumberData
import soa.routing.internal.Parity

private val logger = LoggerFactory.getLogger("soa.Lab5")

@Configuration
class OddFlows {
    /** Filters, translates NumberData to OddNumberData and handles it. Examine the filter carefully. */
    @Bean
    fun oddFlow(): IntegrationFlow =
        integrationFlow("oddChannel") {
            filter { m: NumberData ->
                val passes = Parity.isEven(m.data)
                logger.info("  🔍 Odd Filter: checking {} → {}", m.data, if (passes) "PASS" else "REJECT")
                passes
            }
            transform { m: NumberData ->
                val odd = OddNumberData(m.data)
                logger.info("  ⚙️  Odd Transformer: {} → {}", m, odd)
                odd
            }
            handle<OddNumberData> { odd, _ ->
                logger.info("  ✅ Odd Handler: Processed [{}]", odd)
            }
        }

    /** Handles discarded messages. */
    @Bean
    fun discarded(): IntegrationFlow =
        integrationFlow("discardChannel") {
            handle { p ->
                logger.info("  🗑️  Discard Handler: [{}]", p.payload)
            }
        }
}
