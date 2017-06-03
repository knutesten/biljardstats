package com.billiardsstats.web.websocket.protocol.out

/**
 * @author: Knut Esten Melandsø Nekså
 */
class MessageOut(val type: MessageOutType, val payload: Any)

enum class MessageOutType {
    ALL_USERS, USER_CONNECTED, USER_DISCONNECTED, PING, GAME_REQUEST, GAME_REQUEST_REJECTED, GAME_REQUEST_ACCEPTED
}