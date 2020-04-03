package com.github.rkbalgi.demo.services.kotlin

import org.eclipse.microprofile.config.inject.ConfigProperty
import java.net.InetAddress
import java.time.LocalDateTime
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Response

@Path("/kotlin")
class KotlinTestResource {

    @Inject
    lateinit var healthProvider: HealthProvider
    private val okResponseValue = "We're healthy!"

    @Inject
    @ConfigProperty(name = "quarkus.http.port")
    var serverPort: Int = 0


    @GET
    @Path("/test")
    fun test(): String {
        return "Hello there!!. The server is running @  ${InetAddress.getLocalHost()}: $serverPort and the time is ${LocalDateTime.now()}"
    }


    @GET
    @Path("/set_health")
    fun setHealth(@QueryParam("value") value: Boolean) {
        healthProvider.healthy = value
    }

    @GET
    @Path("/healthy")
    fun isHealthy(): Response {
        if (!healthProvider.isHealthy()) {
            return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
        return Response.ok(okResponseValue).build();
    }
}

