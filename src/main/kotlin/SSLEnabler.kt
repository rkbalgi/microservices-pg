package com.github.rkbalgi.demo.services.kotlin

import io.quarkus.runtime.Startup
import io.quarkus.runtime.StartupEvent
import org.eclipse.microprofile.config.ConfigProvider
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.context.Initialized
import javax.enterprise.event.Observes
import javax.ws.rs.ApplicationPath

@Startup
@ApplicationScoped
class SSLEnabler {


    fun initialized(@Observes event: StartupEvent) {
        println("application initialized $event")


    }
}