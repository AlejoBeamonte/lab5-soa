package soa.numbers

/** The application event the numbers module publishes, and the payload of every message. */
data class NumberData(
    val data: Int,
)
