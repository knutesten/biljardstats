package com.billiardsstats.web.game

import com.billiardsstats.read.user.User

/**
 * @author: Knut Esten Melandsø Nekså
 */
data class EightBall(val challenger: User, val opponent: User) : Game
