package uk.ashleybye.rxweb.retrofit

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.julienviet.retrofit.vertx.VertxCallFactory
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import io.reactivex.Observable
import io.vertx.core.Vertx
import io.vertx.core.http.HttpClientOptions
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import sun.security.provider.certpath.Vertex
import uk.ashleybye.rxweb.models.TflArrival
import uk.ashleybye.rxweb.models.TflLine
import uk.ashleybye.rxweb.models.TflStation


interface TflRetrofit {
    @GET("Line/Mode/tube")
    fun getAllUndergroundLines(): Observable<List<TflLine>>

    @GET("/Line/{lineName}/StopPoints")
    fun getStationsForUndergroundLine(
            @Path("lineName") lineName: String
    ): Observable<List<TflStation>>

    @GET("/Line/{lineName}/Arrivals")
    fun getArrivalsFor(
            @Path("lineName") lineName: String,
            @Query("stopPointId") stationNaptanId: String
    ) : Observable<List<TflArrival>>
}

object KodeinTflRetrofit {
    val module = Kodein.Module {
        val mapper = ObjectMapper()
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

        val client = Vertx.currentContext().owner().createHttpClient(HttpClientOptions().setDefaultPort(8081))
        val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://api.tfl.gov.uk/")
                .callFactory(VertxCallFactory(client))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .build()

        val tflRetrofit: TflRetrofit = retrofit.create(TflRetrofit::class.java)

        bind() from instance(tflRetrofit)
    }
}
