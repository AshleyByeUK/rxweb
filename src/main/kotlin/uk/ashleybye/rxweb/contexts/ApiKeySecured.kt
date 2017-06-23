package uk.ashleybye.rxweb.contexts

import com.github.salomonbrys.kodein.conf.KodeinGlobalAware
import io.vertx.ext.web.RoutingContext
import uy.kohesive.kovert.core.HttpErrorUnauthorized


class ApiKeySecured(private val routingContext: RoutingContext) : KodeinGlobalAware {
    val user: String = routingContext.request().getParam("appid") ?: throw HttpErrorUnauthorized()
}