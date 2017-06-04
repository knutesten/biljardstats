package com.billiardsstats.read.game

import com.billiardsstats.read.user.User

/**
 * @author: Knut Esten Melandsø Nekså
 */
data class EightBallGame(override val id: String, override val challenger: User, override val opponent: User) : Game {
    override val type = GameType.EIGHT_BALL
}
