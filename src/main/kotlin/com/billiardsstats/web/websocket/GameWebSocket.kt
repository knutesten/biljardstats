package com.billiardsstats.web.websocket

import com.auth0.jwt.exceptions.JWTVerificationException
import com.billiardsstats.read.user.UserDao
import com.billiardsstats.web.SparkWebSocketService
import com.billiardsstats.web.auth.JwtTokenUtil
import com.billiardsstats.web.toJson
import com.billiardsstats.web.websocket.protocol.`in`.AcceptGameRequest
import com.billiardsstats.web.websocket.protocol.`in`.CreateGameRequest
import com.billiardsstats.web.websocket.protocol.`in`.MessageIn
import com.billiardsstats.web.websocket.protocol.`in`.RejectGameRequest
import com.billiardsstats.web.websocket.protocol.out.MessageOut
import com.billiardsstats.web.websocket.protocol.out.MessageOutType
import com.fasterxml.jackson.databind.ObjectMapper
import org.eclipse.jetty.websocket.api.Session
import org.eclipse.jetty.websocket.api.WebSocketException
import org.eclipse.jetty.websocket.api.annotations.*
import org.springframework.stereotype.Component
import spark.Spark.webSocket
import java.util.*
import javax.annotation.PostConstruct
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
open class GameWebSocketHandler(private val jwtTokenUtil: JwtTokenUtil,
                                private val userDao: UserDao,
                                private val messageHandler: MessageHandler) {
    @PostConstruct
    fun init() {
        Timer("Remove closed sessions", true).scheduleAtFixedRate(0, 5000) {
            SessionManager.sessions().forEach {
                try {
                    it.remote.sendString(toJson.render(MessageOut(MessageOutType.PING, "")))
                } catch (ioe: WebSocketException) {
                    closeSession(it)
                }
            }
        }
    }

    @OnWebSocketConnect
    fun onConnect(session: Session) {
        try {
            val user = userDao.findById(session.verifyTokenAndReturnUserId())
            val oldConnectionExists = SessionManager.openConnectionForUserExists(user)

            if (oldConnectionExists) {
                val oldSession = SessionManager.session(user)
                SessionManager.remove(oldSession)
                oldSession.close(1000, "An open connection already exists for the user, closing the old connection.")
            }

            println("User connected ${user.email} (${session.hashCode()})")
            SessionManager.add(session, user)

            messageHandler.sendUserList(session)
            if (!oldConnectionExists) {
                messageHandler.broadcastNewUserConnected(user)
            }
        } catch(e: Exception) {
            session.close(1000, "User could not be verified.")
        }
    }

    @OnWebSocketMessage
    fun onMessage(session: Session, jsonMessage: String) {
        val payload = ObjectMapper()
                .readValue(jsonMessage, MessageIn::class.java)
                .decodePayload()
        when (payload) {
            is CreateGameRequest -> messageHandler.handleCreateGameRequest(session, payload)
            is RejectGameRequest -> messageHandler.handleRejectGameRequest(session, payload)
            is AcceptGameRequest -> messageHandler.handleAcceptGameRequest(payload)
        }
    }

    @OnWebSocketError
    fun onError(session: Session, t: Throwable) {
        println(session.hashCode().toString() + " " + t.message)
    }

    @OnWebSocketClose
    fun onClose(session: Session, statusCode: Int, reason: String) {
        closeSession(session)
    }

    private fun closeSession(session: Session) {
        if (SessionManager.containsSession(session)) {
            val user = SessionManager.user(session)
            messageHandler.broadcastUserDisconnected(user)
        }
        SessionManager.remove(session)
    }

    private fun Session.verifyTokenAndReturnUserId(): String {
        val token = this.upgradeRequest.cookies?.find { it.name == "jwt" }?.value
                ?: throw JWTVerificationException("No jwt token in cookies.")
        return jwtTokenUtil.verifyAndReturnUserId(token)
    }
}

fun Session.sendJsonAsync(type: MessageOutType, obj: Any = "") {
    try {
        this.remote.sendStringByFuture(toJson.render(MessageOut(type, obj)))
    } catch (e: WebSocketException) {
        println("Error sending message to ${SessionManager.user(this).email} (${this.hashCode()}): ${e.message}")
    }
}
