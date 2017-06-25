package uk.ashleybye.rxweb.retrofit

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.conf.global
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
import uk.ashleybye.rxweb.config.Configuration
import uk.ashleybye.rxweb.models.TflArrival
import uk.ashleybye.rxweb.models.TflLine
import uk.ashleybye.rxweb.models.TflStation


interface TflRetrofit {
    @GET("Line/Mode/tube")
    fun getAllUndergroundLines(
            @Query("app_key") appKey: String = Kodein.global.instance<Configuration>().apiKeys.tfl.appKey,
            @Query("appId") appId: String = Kodein.global.instance<Configuration>().apiKeys.tfl.appId
    ): Observable<List<TflLine>>

    @GET("/Line/{lineName}/StopPoints")
    fun getStationsForUndergroundLine(
            @Path("lineName") lineName: String,
            @Query("app_key") appKey: String = Kodein.global.instance<Configuration>().apiKeys.tfl.appKey,
            @Query("appId") appId: String = Kodein.global.instance<Configuration>().apiKeys.tfl.appId
    ): Observable<List<TflStation>>

    @GET("/Line/{lineName}/Arrivals")
    fun getArrivalsFor(
            @Path("lineName") lineName: String,
            @Query("stopPointId") stationNaptanId: String,
            @Query("app_key") appKey: String = Kodein.global.instance<Configuration>().apiKeys.tfl.appKey,
            @Query("app_id") appId: String = Kodein.global.instance<Configuration>().apiKeys.tfl.appId
    ) : Observable<List<TflArrival>>
}

object KodeinTflRetrofit {
    val module = Kodein.Module {
        val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://api.tfl.gov.uk/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

        val tflRetrofit: TflRetrofit = retrofit.create(TflRetrofit::class.java)

        bind() from instance(tflRetrofit)
    }
}
