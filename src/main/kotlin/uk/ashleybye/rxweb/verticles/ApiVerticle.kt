package uk.ashleybye.rxweb.verticles

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.conf.global
import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import uk.ashleybye.rxweb.api.UndergroundRestController
import uk.ashleybye.rxweb.services.KodeinTflService
import uy.klutter.core.common.initializedBy
import uy.kohesive.kovert.vertx.bindController


class ApiVerticle : AbstractVerticle() {

    override fun start(startFuture: Future<Void>?) {
        // Initialise injection.
        configureKodein()

        val apiRouter = configureRouter(vertx)

        vertx.createHttpServer()
                .requestHandler { apiRouter.accept(it) }
                .listen(8080)
    }

    private fun configureKodein() {
        // Injection starting point.
        Kodein.global.addImport(Kodein.Module {
            // Import services.
            import(KodeinTflService.module)
        })
    }

    private fun configureRouter(vertx: Vertx): Router {
        val apiMountPoint = "api"

        val routerInit = fun Router.() {
            // Bind the controller classes.
            bindController(UndergroundRestController(), apiMountPoint)
        }

        val router = Router.router(vertx) initializedBy { router ->
            router.routerInit()
        }

        return router
    }
}