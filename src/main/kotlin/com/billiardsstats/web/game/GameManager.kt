package com.billiardsstats.web.game

import com.billiardsstats.read.game.GameType
import com.billiardsstats.read.user.User
import java.util.*

/**
 * @author: Knut Esten Melandsø Nekså
 */
object GameManager {
    private val games: MutableMap<String, GameEntry> = mutableMapOf()
    private val users: MutableMap<User, String> = mutableMapOf()

    class GameEntry(val id: String, val challenger: User, val opponent: User, val gameType: GameType)

    fun createGame(challenger: User, opponent: User, gameType: GameType): String {
        val id = UUID.randomUUID().toString()
        games[id] = GameEntry(id, challenger, opponent, gameType)
        users[challenger] = id
        users[opponent] = id

        return id
    }

    fun userIsPlaying(user: User) = users.containsKey(user)

    fun game(user: User) = games[users[user]]!!

    fun game(id: String) = games[id]!!

    fun gameType(id: String) = games[id]!!.gameType

    fun abortGame(gameEntry: GameEntry) {
        games.remove(gameEntry.id)
        users.remove(gameEntry.challenger)
        users.remove(gameEntry.opponent)
    }
}
