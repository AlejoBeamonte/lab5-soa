package soa.even

/** An even number, after the even Message Translator. It refuses to hold an odd number. */
data class EvenNumberData(
    val data: Int,
) {
    init {
        require(data % 2 == 0) { "$data is not even" }
    }
}
