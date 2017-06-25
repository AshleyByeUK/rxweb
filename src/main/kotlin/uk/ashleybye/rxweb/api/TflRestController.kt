package uk.ashleybye.rxweb.api

import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.reactivex.Scheduler
import io.reactivex.observables.ConnectableObservable
import io.reactivex.schedulers.Schedulers
import io.vertx.core.http.HttpHeaders
import io.vertx.ext.web.RoutingContext
import uk.ashleybye.rxweb.models.TflArrival
import uk.ashleybye.rxweb.models.TflLine
import uk.ashleybye.rxweb.models.TflStation
import uk.ashleybye.rxweb.services.TflRestService


class TflRestController() {

    private val APPLICATION_JSON = "application/json"
    private val MOSHI = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    fun getLines(routingContext: RoutingContext,
                 subscribeScheduler: Scheduler = Schedulers.io(),
                 observeScheduler: Scheduler = Schedulers.io()) {

        val type = Types.newParameterizedType(List::class.java, TflLine::class.java)
        val jsonAdapter = MOSHI.adapter<List<TflLine>>(type)

        ConnectableObservable
                .defer { TflRestService.getLines(subscribeScheduler, observeScheduler) }
                .subscribe(
                        { lines ->
                            val json = jsonAdapter.toJson(lines)
                            routingContext
                                    .response()
                                    .putHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
                                    .end(json)
                        },
                        { it.printStackTrace() }
                )
    }

    fun getStations(routingContext: RoutingContext,
                    subscribeScheduler: Scheduler = Schedulers.io(),
                    observeScheduler: Scheduler = Schedulers.io()) {

        val type = Types.newParameterizedType(List::class.java, TflStation::class.java)
        val jsonAdapter = MOSHI.adapter<List<TflStation>>(type)

        val line = routingContext.request().getParam("line")

        ConnectableObservable
                .defer { TflRestService.getStations(line, subscribeScheduler, observeScheduler) }
                .subscribe(
                        { stations ->
                            val json = jsonAdapter.toJson(stations)
                            routingContext
                                    .response()
                                    .putHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
                                    .end(json)
                        },
                        { it.printStackTrace() }
                )
    }

    fun getArrivals(routingContext: RoutingContext,
                    subscribeScheduler: Scheduler = Schedulers.io(),
                    observeScheduler: Scheduler = Schedulers.io()) {

        val type = Types.newParameterizedType(List::class.java, TflArrival::class.java)
        val jsonAdapter = MOSHI.adapter<List<TflArrival>>(type)

        val line = routingContext.request().getParam("line")
        val station = routingContext.request().getParam("station")

        ConnectableObservable
                .defer { TflRestService.getArrivals(line, station, subscribeScheduler, observeScheduler) }
                .subscribe(
                        { arrivals ->
                            val json = jsonAdapter.toJson(arrivals)
                            routingContext
                                    .response()
                                    .putHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
                                    .end(json)
                        },
                        { it.printStackTrace() }
                )
    }
}