package com.billiardsstats.web.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

/**
 * @author: Knut Esten Melandsø Nekså
 */
@Component
class JwtTokenUtil(@Value("\${jwt.secret}") private val secret: String) {
    private val hashingAlgorithm = Algorithm.HMAC512(secret)
    private val validator = JWT.require(hashingAlgorithm).build()

    fun createTokenForUser(subject: String) = JWT
            .create()
            .withSubject(subject)
            .withExpiresAt(
                    Date.from(LocalDateTime
                            .now()
                            .plusDays(2)
                            .atZone(ZoneId.systemDefault()).toInstant()))
            .sign(hashingAlgorithm)

    fun verifyAndReturnUserId(token: String): String {
        val verifiedToken = validator.verify(token)
        return verifiedToken.subject
    }
}