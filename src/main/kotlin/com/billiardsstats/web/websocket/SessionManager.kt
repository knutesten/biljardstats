package com.billiardsstats.web.websocket

import com.billiardsstats.read.user.User
import org.eclipse.jetty.websocket.api.Session

/**
 * @author: Knut Esten Melandsø Nekså
 */
object SessionManager {
    private val sessions: MutableMap<Session, User> = mutableMapOf()

    fun openConnectionForUserExists(user: User): Boolean = sessions.containsValue(user)

    fun add(session: Session, user: User) {
        sessions[session] = user
    }

    fun remove(session: Session) {
        sessions.remove(session)
    }

    fun user(session: Session) = sessions[session]!!

    fun users() = sessions.values
    fun sessions() = sessions.keys
    fun session(user: User) = sessions.entries.find { entries -> entries.value == user }!!.key
    fun containsSession(session: Session) = sessions.containsKey(session)

}