package com.billiardsstats.web.websocket.protocol.`in`

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import kotlin.reflect.KClass

/**
 * @author: Knut Esten Melandsø Nekså
 */
class MessageIn(@JsonProperty("type") val type: MessageInType, @JsonProperty("payload") val payload: JsonNode) {
    fun decodePayload() = ObjectMapper().treeToValue(payload, type.clazz.java)!!
}

enum class MessageInType(val clazz: KClass<out Any>) {
    CREATE_GAME_REQUEST(CreateGameRequest::class),
    REJECT_GAME_REQUEST(RejectGameRequest::class),
    ACCEPT_GAME_REQUEST(AcceptGameRequest::class)
}