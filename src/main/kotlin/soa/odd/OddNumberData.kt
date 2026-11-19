package soa.odd

/** An odd number, after the odd Message Translator. It refuses to hold an even number. */
data class OddNumberData(
    val data: Int,
) {
    init {
        require(data % 2 != 0) { "$data is not odd" }
    }
}
