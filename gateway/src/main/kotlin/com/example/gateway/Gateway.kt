package com.example.gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain


@SpringBootApplication
@EnableWebFluxSecurity
class Gateway {
    @Bean
    fun securityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        http.authorizeExchange {
            it.pathMatchers("/public/**").permitAll()
                .anyExchange().authenticated()
        }.oauth2Login(Customizer.withDefaults())
        return http.build()
    }
}

fun main(args: Array<String>) {
    runApplication<Gateway>(*args)
}
