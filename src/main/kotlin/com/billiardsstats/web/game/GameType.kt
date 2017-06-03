package com.billiardsstats.web.game

import kotlin.reflect.KClass

/**
 * @author: Knut Esten Melandsø Nekså
 */
enum class GameType(val clazz: KClass<out Game>) {
    EIGHT_BALL(EightBall::class)
}