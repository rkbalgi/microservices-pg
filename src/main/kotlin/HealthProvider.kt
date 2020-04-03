package com.github.rkbalgi.demo.services.kotlin

import javax.enterprise.context.ApplicationScoped
import javax.inject.Singleton

@ApplicationScoped
class HealthProvider() {

    @Volatile
    var healthy = true

    fun isHealthy(): Boolean = healthy


}