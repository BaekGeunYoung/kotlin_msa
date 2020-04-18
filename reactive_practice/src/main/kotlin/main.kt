import reactor.core.publisher.Flux
import reactor.core.publisher.GroupedFlux
import reactor.core.publisher.Mono
import java.util.function.BiFunction

fun main() {
    val basket1 =
        listOf("kiwi", "orange", "lemon", "orange", "lemon", "kiwi")
    val basket2 =
        listOf("banana", "lemon", "lemon", "kiwi")
    val basket3 =
        listOf("strawberry", "orange", "lemon", "grape", "strawberry")
    val baskets =
        listOf(basket1, basket2, basket3)
    val basketFlux: Flux<List<String>> = Flux.fromIterable(baskets)

//    basketFlux.concatMap { basket ->
//        val groupedFruits = Flux.fromIterable(basket).groupBy{fruit -> fruit}
//        return@concatMap groupedFruits
//    }.subscribe{println(it)}

    basketFlux.concatMap { basket ->
        val distinctFruits : Mono<List<String>> = Flux.fromIterable(basket).distinct().collectList()
        val countFruitsMono : Mono<MutableMap<String, Long>> = Flux.fromIterable(basket)
            .groupBy { fruit -> fruit }
            .concatMap { groupedFlux ->
                groupedFlux!!.count().map { count ->
                    val fruitCount: MutableMap<String, Long> = mutableMapOf()
                    fruitCount[groupedFlux.key()!!] = count
                    return@map fruitCount
                }
            }
            .reduce{ accumulatedMap, currentMap ->
                val ret = mutableMapOf<String, Long>()
                ret.putAll(accumulatedMap)
                ret.putAll(currentMap)

                return@reduce ret
            }

        val comb = BiFunction { distinct: List<String>, count: MutableMap<String, Long> -> FruitInfo(distinct, count)}
        Flux.zip(distinctFruits, countFruitsMono, comb)
    }.subscribe{ println(it) }
}