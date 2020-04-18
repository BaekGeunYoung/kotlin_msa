import reactor.core.publisher.Flux
import reactor.core.publisher.GroupedFlux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
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

    val countDownLatch = CountDownLatch(5)
    basketFlux.flatMapSequential { basket ->
        val distinctFruits : Mono<List<String>> = Flux.fromIterable(basket).distinct().collectList().subscribeOn(Schedulers.parallel())
        val countFruitsMono : Mono<MutableMap<String, Long>> = Flux.fromIterable(basket)
            .groupBy { fruit -> fruit }
            .flatMapSequential { groupedFlux: GroupedFlux<String, String> ->
                groupedFlux.count().map { count ->
                    val fruitCount: MutableMap<String, Long> = mutableMapOf()
                    fruitCount[groupedFlux.key()!!] = count
                    return@map fruitCount
                }
            }
            .reduce { accumulatedMap, currentMap ->
                val ret = mutableMapOf<String, Long>()
                ret.putAll(accumulatedMap)
                ret.putAll(currentMap)

                return@reduce ret
            }
            .subscribeOn(Schedulers.parallel())

        val comb = BiFunction { distinct: List<String>, count: MutableMap<String, Long> -> FruitInfo(distinct, count)}
        Flux.zip(distinctFruits, countFruitsMono, comb)
    }.subscribe({ println(it) }, {countDownLatch.countDown()}, {countDownLatch.countDown()})

    countDownLatch.await(2, TimeUnit.SECONDS)
}

fun test() {
    val flux = Flux.just(1, 2, 3)
    val mono = Mono.just(10)

    val test = mono.map {
        val ret: MutableMap<String, Int> = mutableMapOf()
        ret["hello"] = it
        return@map ret
    }

    val map: Flux<Flux<Int>> = flux.map { Flux.range(0, it) }
}