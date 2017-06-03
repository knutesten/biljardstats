package com.billiardsstats.read.game

import com.billiardsstats.write.game.EightBallGameCreatedEvent
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component

/**
 * @author: Knut Esten Melandsø Nekså
 */

@Component
open class EightBallGameEventHandler {
    @EventHandler
    fun onEightBallGameCreated(eightBallGameCreatedEvent: EightBallGameCreatedEvent) {
        println("Eight ball game created: " + eightBallGameCreatedEvent.id)
    }
}
