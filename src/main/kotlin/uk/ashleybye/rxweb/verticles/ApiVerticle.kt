package uk.ashleybye.rxweb.verticles

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import uk.ashleybye.rxweb.api.TflRestController
import uk.ashleybye.rxweb.api.TflStreamController
import uk.ashleybye.rxweb.retrofit.KodeinTflRetrofit
import uk.ashleybye.rxweb.services.KodeinTflRestService
import uk.ashleybye.rxweb.services.KodeinTflStreamService


class ApiVerticle : AbstractVerticle() {
    override fun start(startFuture: Future<Void>?) {
        configureKodein()

        val apiRouter = configureRouter(vertx)

        vertx.createHttpServer()
                .requestHandler { apiRouter.accept(it) }
                .listen(8080, { result ->
                    if (result.succeeded()) {
                        startFuture?.complete()
                    } else {
                        startFuture?.fail(result.cause())
                        //TODO(Ash): Log it.
                    }
                })
    }

    private fun configureKodein() {
        // Injection starting point.
        Kodein.global.addImport(Kodein.Module {
            // Import services.
            import(KodeinTflRetrofit.module)
            import(KodeinTflStreamService.module)
            import(KodeinTflRestService.module)
        })
    }

    private fun configureRouter(vertx: Vertx): Router {
        val pushApiMountPoint = "/push-api/underground"
        val restApiMountPoint = "/api/underground"
        val router = Router.router(vertx)

        router.get("$pushApiMountPoint/lines").handler(TflStreamController().streamLines())
        router.get("$pushApiMountPoint/lines/:line/stations").handler(TflStreamController().streamStations())
        router.get("$pushApiMountPoint/lines/:line/:station/arrivals").handler(TflStreamController().streamArrivals())

        router.get("$restApiMountPoint/lines").handler { TflRestController().getLines(it) }
        router.get("$restApiMountPoint/lines/:line/stations").handler { TflRestController().getStations(it) }
        router.get("$restApiMountPoint/lines/:line/:station/arrivals").handler { TflRestController().getArrivals(it) }

        return router
    }
}