package com.billiardsstats.web.rest

import com.billiardsstats.web.SparkService
import com.billiardsstats.web.auth.OpenIdConnectAuth
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.DataAccessException
import org.springframework.stereotype.Component
import spark.Spark.get
import spark.Spark.halt
import java.math.BigInteger
import java.security.SecureRandom

@Component
open class AuthService(val openIdConnectAuth: OpenIdConnectAuth,
                       @Value("\${open-id-connect.logout-url") val logoutUrl: String) : SparkService {
    override fun init() {
        get("/api/auth/login") { req, res ->
            val state = BigInteger(130, SecureRandom()).toString(32)
            req.session(true)
            req.session().attribute("state", state)
            res.redirect(openIdConnectAuth.createAuthenticationUrl(state))
            res
        }

        get("/api/auth/code") { req, res ->
            if (req.queryParams("state") != req.session().attribute<String>("state")) {
                req.session().invalidate()
                println(req.url())
                halt(401, "Invalid state parameter.")
            }

            try {
                val email = openIdConnectAuth.exchangeCodeForEmail(req.queryParams("code"))
                req.session().attribute("user", email)
                res.redirect("/")
                res
            } catch (_: DataAccessException) {
                halt(401, "You are not a registered user.")
            }
        }

        get("/api/auth/logout") { req, res ->
            req.session().invalidate()
            res.redirect(logoutUrl)
            res
        }

        get("/api/auth/session", { req, _ -> req.session().attribute("user") })
    }
}