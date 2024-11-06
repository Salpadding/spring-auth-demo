package com.example.resource

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.RegexRequestMatcher
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@EnableMethodSecurity
class Resource {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests {
            it.requestMatchers(RegexRequestMatcher("^/public.*", null)).permitAll()
                .anyRequest().authenticated()
        }.oauth2ResourceServer {
            it.jwt(Customizer.withDefaults())
        }
        return http.build()
    }

    @Bean
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val grantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()
        // claim = roles
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles")
        // prefix with role
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_")

        val jwtAuthenticationConverter = JwtAuthenticationConverter()
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter)
        return jwtAuthenticationConverter
    }
}

fun main(args: Array<String>) {
    runApplication<Resource>(*args)
}

@RestController
@RequestMapping("/api/v1/resource")
class Api {
    @GetMapping("/me")
    fun me(): Any {
        val jwt =  SecurityContextHolder.getContext().authentication
            .let { it as JwtAuthenticationToken }.token.claims
        val authz = SecurityContextHolder.getContext().authentication.authorities
        return mapOf("jwt" to jwt, "authz" to authz)
    }

    @PreAuthorize("hasAuthority('ROLE_admin')")
    @GetMapping("/admin")
    fun admin(): Any {
        return "OK"
    }
}