package uk.ashleybye.rxweb.retrofit

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
            @Query("app_key") appKey: String = Configuration.tflApi_appKey,
            @Query("appId") appId: String = Configuration.tflApi_appId
    ): Observable<List<TflLine>>

    @GET("/Line/{lineName}/StopPoints")
    fun getStationsForUndergroundLine(
            @Path("lineName") lineName: String,
            @Query("app_key") appKey: String = Configuration.tflApi_appKey,
            @Query("appId") appId: String = Configuration.tflApi_appId
    ): Observable<List<TflStation>>

    @GET("/Line/{lineName}/Arrivals")
    fun getArrivalsFor(
            @Path("lineName") lineName: String,
            @Query("stopPointId") stationNaptanId: String,
            @Query("app_key") appKey: String = Configuration.tflApi_appKey,
            @Query("appId") appId: String = Configuration.tflApi_appId
    ) : Observable<List<TflArrival>>

    companion object {
        private val moshi: Moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

        private val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://api.tfl.gov.uk/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

        val instance: TflRetrofit = retrofit.create(TflRetrofit::class.java)
    }
}
