package com.billiardsstats.web.rest

import com.billiardsstats.read.user.UserDao
import com.billiardsstats.web.SparkService
import com.billiardsstats.web.auth.OpenIdConnectAuth
import com.billiardsstats.web.toJson
import com.billiardsstats.write.user.CreateUserCommand
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.DataAccessException
import org.springframework.stereotype.Component
import spark.Route
import spark.Spark.get
import spark.Spark.halt
import java.math.BigInteger
import java.security.SecureRandom

@Component
open class AuthService(private val openIdConnectAuth: OpenIdConnectAuth,
                       private val userDao: UserDao,
                       private val commandGateway: CommandGateway,
                       @Value("\${open-id-connect.logout-url") private val logoutUrl: String) : SparkService {
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
                halt(401, "Invalid state parameter.")
            }

            val user = openIdConnectAuth.exchangeCodeForUser(req.queryParams("code"))
            req.session().attribute("user", user)

            try {
                userDao.findById(user.id)
            } catch (_: DataAccessException) {
                commandGateway.send<Unit>(CreateUserCommand(user.id, user.email, user.givenName, user.familyName))
            }

            res.redirect("/")
            res
        }

        get("/api/auth/logout") { req, res ->
            req.session().invalidate()
            res.redirect(logoutUrl)
            res
        }

        get("/api/auth/session", Route { req, _ -> req.session().attribute("user") }, toJson)
    }
}