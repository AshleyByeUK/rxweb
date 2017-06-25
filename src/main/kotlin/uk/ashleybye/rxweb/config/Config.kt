package uk.ashleybye.rxweb.config

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.singleton
import io.vertx.config.ConfigRetriever
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.config.ConfigRetrieverOptions
import io.vertx.kotlin.config.ConfigStoreOptions
import kotlin.system.exitProcess


class Configuration(private val vertx: Vertx, private val vertxConfig: JsonObject?) {
    private var configJson: JsonObject? = null

    init {
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
                configJson = asyncResult.result()
            }
        }
    }

    val server = Server()
    val apiKeys = ApiKeys()

    inner class Server() {
        private val server = this@Configuration.configJson?.getJsonObject("server")
        val port = server?.getInteger("port") ?: 8080
        val isCachingEnabled = server?.getBoolean("caching") ?: false
    }

    inner class ApiKeys() {

        val tfl = TflApiKeys()

        inner class TflApiKeys() {
            val appId = this@Configuration.configJson?.getString("TFL_APP_ID") ?: ""
            val appKey = this@Configuration.configJson?.getString("TFL_APP_KEY") ?: ""
        }
    }
}

object KodeinConfiguration {
    private lateinit var vertx: Vertx
    private var vertxConfig: JsonObject? = null
    private var isInitialised = false

    fun init(vertx: Vertx, vertxConfig: JsonObject?) {
        this.vertx = vertx
        this.vertxConfig = vertxConfig
        isInitialised = true
    }

    val module = Kodein.Module {
        if (isInitialised) {
            bind() from singleton { Configuration(vertx, vertxConfig) }
        } else {
            throw ConfigurationNotInitialisedException()
        }
    }

    class ConfigurationNotInitialisedException : Exception() {
        override val message = "Configuration has not been initialised"
    }
}