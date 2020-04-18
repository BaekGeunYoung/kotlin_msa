class FruitInfo(
    private val distinctFruits: List<String>?,
    private val countFruits: Map<String, Long>?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val fruitInfo = other as FruitInfo
        if (if (distinctFruits != null) distinctFruits != fruitInfo.distinctFruits else fruitInfo.distinctFruits != null) return false
        return if (countFruits != null) countFruits == fruitInfo.countFruits else fruitInfo.countFruits == null
    }

    override fun hashCode(): Int {
        var result = distinctFruits?.hashCode() ?: 0
        result = 31 * result + (countFruits?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "FruitInfo{" +
                "distinctFruits=" + distinctFruits +
                ", countFruits=" + countFruits +
                '}'
    }
}