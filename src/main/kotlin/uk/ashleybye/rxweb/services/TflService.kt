package uk.ashleybye.rxweb.services

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface TflService {
    @GET("Line/Mode/tube")
    fun getAllUndergroundLines(): Observable<List<UndergroundLine>>

    @GET("/Line/{lineName}/StopPoints")
    fun getStationsForUndergroundLine(
            @Path("lineName") lineName: String
    ): Observable<List<UndergroundStation>>

    @GET("/Line/{lineName}/Arrivals")
    fun getArrivalsFor(
            @Path("lineName") lineName: String,
            @Query("stopPointId") stationNaptanId: String
    ) : Observable<List<Arrival>>
}

data class UndergroundLine(val id: String, val name: String)
data class UndergroundStation(val naptanId: String, val commonName: String)
data class Arrival(
        val platformName: String,
        val towards: String,
        val currentLocation: String,
        val expectedArrival:  String)


object KodeinTflService {
    val module = Kodein.Module {
        val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://api.tfl.gov.uk/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

        val tflService: TflService = retrofit.create(TflService::class.java)

        bind<TflService>() with instance(tflService)
    }
}
