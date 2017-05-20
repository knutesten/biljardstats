package com.billiardsstats.write.user

import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.model.AggregateIdentifier
import org.axonframework.commandhandling.model.AggregateLifecycle
import org.axonframework.eventhandling.EventHandler
import org.axonframework.spring.stereotype.Aggregate


/**
 * @author: Knut Esten Melandsø Nekså
 */
@Aggregate
class User {
    @AggregateIdentifier
    lateinit var id: String
    lateinit var email: String
    lateinit var givenName: String
    lateinit var familyName: String

    constructor()

    @CommandHandler
    constructor(createUserCommand: CreateUserCommand) {
        AggregateLifecycle.apply(
                UserCreatedEvent(
                        createUserCommand.id,
                        createUserCommand.email,
                        createUserCommand.givenName,
                        createUserCommand.familyName))
    }

    @EventHandler
    fun onUserCreated(userCreatedEvent: UserCreatedEvent) {
        this.id = userCreatedEvent.id
        this.email = userCreatedEvent.email
        this.givenName = userCreatedEvent.givenName
        this.familyName = userCreatedEvent.familyName
    }
}