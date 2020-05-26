import reactor.core.publisher.Flux
import reactor.core.publisher.GroupedFlux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.function.BiFunction

fun main() {
    publishTest()
}

fun latch() {
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

fun publishTest() {
    val basket1 =
        listOf("kiwi", "orange", "lemon", "orange", "lemon", "kiwi")
    val basket2 =
        listOf("banana", "lemon", "lemon", "kiwi")
    val basket3 =
        listOf("strawberry", "orange", "lemon", "grape", "strawberry")
    val baskets =
        listOf(basket1, basket2, basket3)
    val basketFlux: Flux<List<String>> = Flux.fromIterable(baskets)

    basketFlux.flatMapSequential { basket ->
        val source = Flux.fromIterable(basket).publish().autoConnect(2)
        val distinctFruits : Mono<List<String>> = source.publishOn(Schedulers.parallel()).distinct().collectList()
        val countFruitsMono : Mono<MutableMap<String, Long>> = source.publishOn(Schedulers.parallel())
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

        val comb = BiFunction { distinct: List<String>, count: MutableMap<String, Long> -> FruitInfo(distinct, count)}
        Flux.zip(distinctFruits, countFruitsMono, comb)
    }.subscribe { println(it) }
}

fun test() {
    val flux = Flux.just(0, 1, 2, 3, 4, 5, 6)
    val flatMap = flux.log().flatMap {
        if (it == 5) {
            var ret = 0
            val list = mutableListOf<Int>()
            for (i in (0 until 100)) {
                ret++
                list.add(ret)
            }

            return@flatMap Flux.fromIterable(list)
        }
        else return@flatMap Flux.range(0, it)
    }

    flatMap.log().subscribe({
        println(it)
    }, {
        println("error")
    }, {
        println("complete")
    })
}