package com.billiardsstats.web.websocket

import com.auth0.jwt.exceptions.JWTVerificationException
import com.billiardsstats.web.SparkWebSocketService
import com.billiardsstats.web.auth.JwtTokenUtil
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.WebSocketException
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage
import org.eclipse.jetty.websocket.api.annotations.WebSocket
import org.springframework.stereotype.Component
import spark.Spark.webSocket
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

/**
 * @author: Knut Esten Melandsø Nekså
 */

@Component
open class WebSocketService(private val gameWebSocketHandler: GameWebSocketHandler) : SparkWebSocketService {
    override fun init() {
        webSocket("/game", gameWebSocketHandler)
    }
}

/**
 * @author: Knut Esten Melandsø Nekså
 */
@Component
@WebSocket
class GameWebSocketHandler(private val jwtTokenUtil: JwtTokenUtil) {

    @OnWebSocketConnect
    fun onConnect(session: Session) {
        try {
            val userId = session.getUserId()
            if (GameUserManagement.openConnectionForUserExists(userId))
                session.close(1000, "An open connection already exists for the user.")
            else GameUserManagement.add(userId, session)
        } catch(e: JWTVerificationException) {
            println("User not verified: " + e.message)
            session.close(1000, "User could not be verified.")
        }
    }

    @OnWebSocketClose
    fun onClose(session: Session, statusCode: Int, reason: String) {
        GameUserManagement.remove(session.getUserId())
    }

    @OnWebSocketMessage
    fun onMessage(session: Session, message: String) {
        println("message")
    }

    private fun Session.getUserId(): String {
        val token = this.upgradeRequest.cookies?.find { it.name == "jwt" }?.value
                ?: throw JWTVerificationException("No jwt token in cookies.")
        return jwtTokenUtil.verifyAndReturnUserId(token)
    }
}

object GameUserManagement {
    private val sessions: MutableMap<String, Session> = mutableMapOf()

    init {
        Timer("Remove closed sessions", true)
                .scheduleAtFixedRate(0, 5000) { removeClosedSessions() }
    }

    fun removeClosedSessions() {
        sessions.keys.forEach { userId ->
            try {
                sessions[userId]!!.remote.sendString("ping")
            } catch(ioe: WebSocketException) {
                sessions.remove(userId)
            }
        }
    }

    fun openConnectionForUserExists(userId: String): Boolean = sessions[userId]?.isOpen ?: false

    fun add(userId: String, session: Session) {
        sessions[userId] = session
    }

    fun remove(userId: String) {
        sessions.remove(userId)
    }
}