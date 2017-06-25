package uk.ashleybye.rxweb.config

import io.vertx.config.ConfigRetriever
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.config.ConfigRetrieverOptions
import io.vertx.kotlin.config.ConfigStoreOptions
import kotlin.system.exitProcess


object Configuration {
    private lateinit var configJson: JsonObject
    private val server by lazy { configJson.getJsonObject("server") }

    val server_port by lazy { server.getInteger("port") ?: 8080 }
    val server_isCachingEnabled by lazy { server.getBoolean("caching") ?: false }

    val tflApi_appId by lazy { configJson.getString("TFL_APP_ID") ?: "" }
    val tflApi_appKey by lazy { configJson.getString("TFL_APP_KEY") ?: "" }

    fun init(vertx: Vertx, vertxConfig: JsonObject? = null) {
        val envStore = ConfigStoreOptions()
                .setType("env")

        val jsonStore = ConfigStoreOptions()
                .setType("json")
                .setConfig(vertxConfig)

        val options = ConfigRetrieverOptions()
                .setScanPeriod(5000)
                .addStore(envStore)
                .addStore(jsonStore)

        val retriever = ConfigRetriever.create(vertx, options)
        retriever.getConfig { asyncResult ->
            if (asyncResult.failed()) {
                //TODO(Ash): Log it.
                println("Failed to load config: ${asyncResult.cause()}")
                exitProcess(0)
            } else {
                configJson = asyncResult.result() ?: JsonObject()
            }
        }
    }
}
