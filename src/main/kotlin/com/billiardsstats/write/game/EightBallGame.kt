package com.billiardsstats.write.game

import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.model.AggregateIdentifier
import org.axonframework.commandhandling.model.AggregateLifecycle
import org.axonframework.eventhandling.EventHandler
import org.axonframework.spring.stereotype.Aggregate

/**
 * @author: Knut Esten Melandsø Nekså
 */
@Aggregate
class EightBallGame {
    @AggregateIdentifier
    lateinit var id: String
    lateinit var challengerId: String
    lateinit var opponentId: String

    constructor()

    @CommandHandler
    constructor(createEightBallGameCommand: CreateEightBallGameCommand) {
        AggregateLifecycle.apply(
                EightBallGameCreatedEvent(
                        createEightBallGameCommand.id,
                        createEightBallGameCommand.challengerId,
                        createEightBallGameCommand.opponentId))
    }

    @EventHandler
    fun onEightBallGameCreated(eightBallGameCreatedEvent: EightBallGameCreatedEvent) {
        this.id = eightBallGameCreatedEvent.id
        this.challengerId = eightBallGameCreatedEvent.challengerId
        this.opponentId = eightBallGameCreatedEvent.opponentId
    }
}