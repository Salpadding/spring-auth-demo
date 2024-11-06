package com.example.auth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer
import org.springframework.security.provisioning.JdbcUserDetailsManager
import javax.sql.DataSource


@SpringBootApplication
class Auth {
    // create embedded datasource
    @Bean
    fun dataSource(): DataSource {
        return EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
            .addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
            .build()
    }

    // create pre defined users
    @Bean
    fun users(dataSource: DataSource): UserDetailsService {
        val mgr = JdbcUserDetailsManager(dataSource)
        val users = arrayOf(
            arrayOf("user", "{noop}password", "user"),
            arrayOf("admin", "{noop}password", "user,admin"),
        )

        users.forEach {
            User.builder().username(it[0]).password(it[1]).roles(*it[2].split(",").toTypedArray()).build()
                .let { mgr.createUser(it) }
        }
        return mgr
    }

    // put roles into jwt
    @Bean
    fun jwtTokenCustomizer(): OAuth2TokenCustomizer<JwtEncodingContext> {
        return OAuth2TokenCustomizer {
            if (!OAuth2TokenType.ACCESS_TOKEN.equals(it.tokenType)) return@OAuth2TokenCustomizer
            it.claims.claims { claim ->
                val roles =
                    it.getPrincipal<Authentication>().authorities.let { auth -> AuthorityUtils.authorityListToSet(auth) }
                        .map { c -> c.removePrefix("ROLE_") }
                        .toSet()
                claim["roles"] = roles
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<Auth>(*args)
}