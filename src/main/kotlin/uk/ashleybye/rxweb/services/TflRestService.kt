package uk.ashleybye.rxweb.services

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.refSingleton
import com.github.salomonbrys.kodein.softReference
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import uk.ashleybye.rxweb.models.TflArrival
import uk.ashleybye.rxweb.models.TflLine
import uk.ashleybye.rxweb.models.TflStation
import uk.ashleybye.rxweb.retrofit.TflRetrofit


class TflRestService(private val tflRetrofit: TflRetrofit = Kodein.global.instance()) {

    fun getLines(subscribeScheduler: Scheduler = Schedulers.io(),
                 observeScheduler: Scheduler = Schedulers.io()
    ): Observable<List<TflLine>> {

        return Observable
                .defer { tflRetrofit.getAllUndergroundLines() }
                .subscribeOn(subscribeScheduler)
                .flatMapIterable { it }
                .distinct { (id) -> id }
                .toList()
                .toObservable()
                .publish()
                .refCount()
                .observeOn(observeScheduler)
    }

    fun getStations(line: String,
                    subscribeScheduler: Scheduler = Schedulers.io(),
                    observeScheduler: Scheduler = Schedulers.io()
    ): Observable<List<TflStation>> {

        return Observable
                .defer { tflRetrofit.getStationsForUndergroundLine(line) }
                .subscribeOn(subscribeScheduler)
                .flatMapIterable { it }
                .distinct { (_, naptanId) -> naptanId }
                .toList()
                .toObservable()
                .publish()
                .refCount()
                .observeOn(observeScheduler)
    }

    fun getArrivals(line: String,
                    station: String,
                    subscribeScheduler: Scheduler = Schedulers.io(),
                    observeScheduler: Scheduler = Schedulers.io()
    ): Observable<List<TflArrival>> {

        return Observable
                .defer { tflRetrofit.getArrivalsFor(line, station) }
                .subscribeOn(subscribeScheduler)
                .observeOn(observeScheduler)
    }
}

object KodeinTflRestService {
    val module = Kodein.Module {
        bind() from refSingleton(softReference) { TflRestService() }
    }
}