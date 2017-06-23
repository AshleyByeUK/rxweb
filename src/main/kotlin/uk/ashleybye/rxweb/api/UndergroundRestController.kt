package uk.ashleybye.rxweb.api

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import nl.komponents.kovenant.Promise
import nl.komponents.kovenant.task
import uk.ashleybye.rxweb.contexts.ApiKeySecured
import uk.ashleybye.rxweb.services.Arrival
import uk.ashleybye.rxweb.services.TflService
import uk.ashleybye.rxweb.services.UndergroundLine
import uk.ashleybye.rxweb.services.UndergroundStation


class UndergroundRestController(val undergroundService: TflService = Kodein.global.instance()) {
    fun ApiKeySecured.listUndergroundLines(): Promise<List<UndergroundLine>, Exception> {
        //TODO(Ash): Should this error be encapsulated in the Observable?
        //TODO(Ash): This is blocking, fix it!
        return task {
            undergroundService
                    .getAllUndergroundLines()
                    .doOnError { println(it) }
//                    .timeout(3, TimeUnit.SECONDS)
//                    .retryWhen { failures ->
//                        failures.zipWith(
//                                Observable.range(1, 10),
//                                { error, attempt ->
//                                    if (attempt < 10) {
//                                        Observable.timer(1, TimeUnit.SECONDS)
//                                    } else {
//                                        //TODO(Ash): Logger.log(error) here?
//                                        Observable.error(HttpErrorCode("Timeout Error", 504))
//                                    }
//                                }).flatMap { it }
//                    }
                    .blockingSingle()
        }
    }

    fun ApiKeySecured.listUndergroundByLineStations(line: String): Promise<List<UndergroundStation>, Exception> {
        return task {
            undergroundService
                    .getStationsForUndergroundLine(line)
                    .doOnError { println(it) }
                    .blockingSingle()
        }
    }

    fun ApiKeySecured.listUndergroundByLineByStationArrivals(line: String, station: String): Promise<List<Arrival>, Exception> {
        return task {
            undergroundService
                    .getArrivalsFor(line, station)
                    .doOnError { println(it) }
                    .blockingSingle()
        }
    }
}