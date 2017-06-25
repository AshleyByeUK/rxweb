package uk.ashleybye.rxweb.verticles

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import uk.ashleybye.rxweb.api.TflRestController
import uk.ashleybye.rxweb.api.TflStreamController
import uk.ashleybye.rxweb.config.Configuration


class ApiVerticle : AbstractVerticle() {

    override fun start(startFuture: Future<Void>?) {
        Configuration.init(vertx, config())
        val apiRouter = configureRouter(vertx)

        vertx.createHttpServer()
                .requestHandler { apiRouter.accept(it) }
                .listen(Configuration.server_port, { result ->
                    if (result.succeeded()) {
                        startFuture?.complete()
                    } else {
                        startFuture?.fail(result.cause())
                        //TODO(Ash): Log it.
                    }
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