package uk.ashleybye.rxweb.api

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import io.reactivex.Scheduler
import io.reactivex.observables.ConnectableObservable
import io.reactivex.schedulers.Schedulers
import io.vertx.ext.web.handler.sse.SSEHandler
import uk.ashleybye.rxweb.models.TflArrival
import uk.ashleybye.rxweb.models.TflLine
import uk.ashleybye.rxweb.models.TflStation
import uk.ashleybye.rxweb.services.TflStreamService


class TflStreamController(private val tflStreamService: TflStreamService = Kodein.global.instance()) {

    private val MOSHI = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    fun streamLines(subscribeScheduler: Scheduler = Schedulers.io(),
                    observeScheduler: Scheduler = Schedulers.io()
    ): SSEHandler {

        val jsonAdapter = MOSHI.adapter<TflLine>(TflLine::class.java)

        val sse = SSEHandler.create()
        sse.connectHandler { connection ->
            ConnectableObservable
                    .defer { tflStreamService.streamLines(subscribeScheduler, observeScheduler) }
                    .subscribe(
                            { line ->
                                val json = jsonAdapter.toJson(line)
                                connection.data(json)
                            },
                            { it.printStackTrace() },
                            { connection.close() }
                    )
        }

        return sse
    }

    fun streamStations(subscribeScheduler: Scheduler = Schedulers.io(),
                       observeScheduler: Scheduler = Schedulers.io()
    ): SSEHandler {

        val jsonAdapter = MOSHI.adapter<TflStation>(TflStation::class.java)

        val sse = SSEHandler.create()
        sse.connectHandler { connection ->
            val line = connection.request().getParam("line")

            ConnectableObservable
                    .defer { tflStreamService.streamStations(line, subscribeScheduler, observeScheduler) }
                    .subscribe(
                            { station ->
                                val json = jsonAdapter.toJson(station)
                                connection.data(json)
                            },
                            { it.printStackTrace() },
                            { connection.close() }
                    )
        }

        return sse
    }

    fun streamArrivals(subscribeScheduler: Scheduler = Schedulers.io(),
                       observeScheduler: Scheduler = Schedulers.io()
    ): SSEHandler {

        val jsonAdapter = MOSHI.adapter<TflArrival>(TflArrival::class.java)

        val sse = SSEHandler.create()
        sse.connectHandler { connection ->
            val line = connection.request().getParam("line")
            val station = connection.request().getParam("station")

            ConnectableObservable
                    .defer { tflStreamService.streamArrivals(line, station, subscribeScheduler, observeScheduler) }
                    .subscribe(
                            { arrival ->
                                val json = jsonAdapter.toJson(arrival)
                                connection.data(json)
                            },
                            { it.printStackTrace() },
                            { connection.close() }
                    )
        }

        return sse
    }
}