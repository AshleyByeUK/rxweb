package uk.ashleybye.rxweb.services

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import uk.ashleybye.rxweb.models.TflArrival
import uk.ashleybye.rxweb.models.TflLine
import uk.ashleybye.rxweb.models.TflStation
import uk.ashleybye.rxweb.retrofit.TflRetrofit
import java.util.concurrent.TimeUnit


object TflStreamService {

    private val tflRetrofit: TflRetrofit = TflRetrofit.instance
    private val STREAM_INTERVAL = 2L

    fun streamLines(subscribeScheduler: Scheduler = Schedulers.io(),
                    observeScheduler: Scheduler = Schedulers.io()
    ): Observable<TflLine> {

        return Observable
                .defer { tflRetrofit.getAllUndergroundLines() }
                .subscribeOn(subscribeScheduler)
                .flatMapIterable { it }
                .distinct { (id) -> id }
                .publish()
                .refCount()
                .observeOn(observeScheduler)
    }

    fun streamStations(line: String,
                       subscribeScheduler: Scheduler = Schedulers.io(),
                       observeScheduler: Scheduler = Schedulers.io()
    ): Observable<TflStation> {

        return Observable
                .defer { tflRetrofit.getStationsForUndergroundLine(line) }
                .subscribeOn(subscribeScheduler)
                .flatMapIterable { it }
                .distinct { (_, naptanId) -> naptanId }
                .publish()
                .refCount()
                .observeOn(observeScheduler)
    }

    fun streamArrivals(line: String,
                       station: String,
                       subscribeScheduler: Scheduler = Schedulers.io(),
                       observeScheduler: Scheduler = Schedulers.io()
    ): Observable<TflArrival> {

        return Observable
                .defer {
                    tflRetrofit
                            .getArrivalsFor(line, station)
                            .repeatWhen {
                                Observable
                                        .interval(0, STREAM_INTERVAL, TimeUnit.SECONDS)
                            }
                }
                .subscribeOn(subscribeScheduler)
                .flatMapIterable { it }
                .publish()
                .refCount()
                .observeOn(observeScheduler)
    }
}