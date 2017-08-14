package com.billiardsstats.web.websocket

import com.billiardsstats.read.game.Game
import com.billiardsstats.read.user.User
import com.billiardsstats.web.game.GameManager
import com.billiardsstats.web.websocket.protocol.`in`.AcceptGameRequest
import com.billiardsstats.web.websocket.protocol.`in`.CreateGameRequest
import com.billiardsstats.web.websocket.protocol.`in`.RejectGameRequest
import com.billiardsstats.web.websocket.protocol.out.GameRequest
import com.billiardsstats.web.websocket.protocol.out.GameRequestAccepted
import com.billiardsstats.web.websocket.protocol.out.MessageOutType.*
import com.billiardsstats.write.game.CreateEightBallGameCommand
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.EventHandler
import org.eclipse.jetty.websocket.api.Session
import org.springframework.stereotype.Component


/**
 * @author: Knut Esten Melandsø Nekså
 */
@Component
open class MessageHandler(private val commandGateway: CommandGateway) {
    fun broadcastNewUserConnected(user: User) {
        val newUserSession = SessionManager.session(user)
        SessionManager
                .sessions()
                .filterNot { it == newUserSession }
                .forEach { it.sendJsonAsync(USER_CONNECTED, user) }
    }

    fun sendUserList(session: Session): Unit {
        val yourself = SessionManager.user(session)
        val allButYourself = SessionManager
                .users()
                .filterNot { it == yourself }
        session.sendJsonAsync(ALL_USERS, allButYourself)
    }

    fun handleCreateGameRequest(session: Session, createGameRequest: CreateGameRequest) {
        val challenger = SessionManager.user(session)
        val opponentSession = SessionManager.session(createGameRequest.opponent)

        if (GameManager.userIsPlaying(createGameRequest.opponent)) {
            session.sendJsonAsync(GAME_REQUEST_REJECTED)
            return
        }

        val id = GameManager.createGame(challenger, createGameRequest.opponent, createGameRequest.gameType)
        opponentSession.sendJsonAsync(GAME_REQUEST, GameRequest(id, createGameRequest.gameType, challenger))
    }

    fun handleRejectGameRequest(session: Session, rejectGameRequest: RejectGameRequest) {
        val game = GameManager.gameEntry(rejectGameRequest.id)
        GameManager.abortGame(game)

        val challengerSession = SessionManager.session(game.challenger)
        challengerSession.sendJsonAsync(GAME_REQUEST_REJECTED)
        session.sendJsonAsync(GAME_REQUEST_REJECTED)
    }

    fun handleAcceptGameRequest(acceptGameRequest: AcceptGameRequest) {
        val game = GameManager.gameEntry(acceptGameRequest.id)
        commandGateway.send<Unit>(CreateEightBallGameCommand(game.id, game.challenger.id, game.opponent.id))
    }

    @EventHandler
    fun handleGameCreated(game: Game) {
        val payload = GameRequestAccepted(game.type, game)
        SessionManager.session(game.challenger).sendJsonAsync(GAME_REQUEST_ACCEPTED, payload)
        SessionManager.session(game.opponent).sendJsonAsync(GAME_REQUEST_ACCEPTED, payload)
    }

    fun broadcastUserDisconnected(user: User) =
            SessionManager
                    .sessions()
                    .forEach { it.sendJsonAsync(USER_DISCONNECTED, user) }

}