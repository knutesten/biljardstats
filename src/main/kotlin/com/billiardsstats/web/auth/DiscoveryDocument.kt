package com.billiardsstats.web.auth

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

val cache: ConcurrentMap<String, Pair<Instant, JsonNode>> = ConcurrentHashMap<String, Pair<Instant, JsonNode>>()

class DiscoveryDocument(val discoveryDocumentUrl: String) {
    fun getAuthorizationEndpoint(): String {
        return getFromCache(discoveryDocumentUrl).get("authorization_endpoint").asText()
    }

    fun getTokenEndpoint(): String {
        return getFromCache(discoveryDocumentUrl).get("token_endpoint").asText()
    }

    fun getFromCache(url: String): JsonNode {
        if (cache.containsKey(url) && cache[url]!!.first.isAfter(Instant.now()))
            return cache[url]!!.second

        val response = OkHttpClient().newCall(Request.Builder()
                .url(url)
                .get()
                .build())
                .execute()

        if (response.code() == 200) {
            val node = ObjectMapper().readTree(response.body().bytes())
            val expiry = Instant.now().plus(response.cacheControl().maxAgeSeconds().toLong(), ChronoUnit.SECONDS)
            cache.put(url, Pair(expiry, node))

            return node
        }

        throw RuntimeException("Kunne ikke hente discovery document url")
    }
}
