package com.billiardsstats.web.game

import com.billiardsstats.read.user.User
import java.util.*

/**
 * @author: Knut Esten Melandsø Nekså
 */
object GameManager {
    private val games: MutableMap<String, Game> = mutableMapOf()
    private val users: MutableMap<User, String> = mutableMapOf()

    class Game(val id: String, val challenger: User, val opponent: User, val gameType: GameType)

    fun createGame(challenger: User, opponent: User, gameType: GameType): String {
        val id = UUID.randomUUID().toString()
        games[id] = Game(id, challenger, opponent, gameType)
        users[challenger] = id
        users[opponent] = id

        return id
    }

    fun userIsPlaying(user: User) = users.containsKey(user)

    fun game(user: User) = games[users[user]]!!

    fun game(id: String) = games[id]!!

    fun gameType(id: String) = games[id]!!.gameType

    fun abortGame(game: Game) {
        games.remove(game.id)
        users.remove(game.challenger)
        users.remove(game.opponent)
    }
}
