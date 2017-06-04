package com.billiardsstats.read.game

import com.billiardsstats.read.user.User

/**
 * @author: Knut Esten Melandsø Nekså
 */
interface Game {
    val id: String
    val challenger: User
    val opponent: User
    val type: GameType
}