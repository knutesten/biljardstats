package com.billiardsstats.web.auth

import com.auth0.jwt.JWT
import com.billiardsstats.read.user.User
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

class OpenIdConnectAuth(private val clientId: String,
                        private val secret: String,
                        private val redirectUri: String,
                        private val discoveryDocument: DiscoveryDocument) {

    fun createAuthenticationUrl(state: String): String {
        return "${discoveryDocument.getAuthorizationEndpoint()}?" +
                "client_id=$clientId&" +
                "response_type=code&" +
                "scope=openid email profile&" +
                "redirect_uri=$redirectUri&" +
                "state=$state"
    }

    fun exchangeCodeForUser(code: String): User {
        val form = FormBody.Builder()
                .add("code", code)
                .add("client_id", clientId)
                .add("client_secret", secret)
                .add("redirect_uri", redirectUri)
                .add("grant_type", "authorization_code")
                .build()

        val request = Request.Builder()
                .url(discoveryDocument.getTokenEndpoint())
                .post(form)
                .build()

        val response = OkHttpClient()
                .newCall(request)
                .execute()

        val token = JWT.decode(ObjectMapper().readTree(response.body().bytes()).get("id_token").asText())

        return User(
                token.getClaim("sub").asString(),
                token.getClaim("email").asString(),
                token.getClaim("given_name").asString(),
                token.getClaim("family_name").asString())
    }
}