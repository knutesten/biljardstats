package com.billiardsstats.read.user

import com.billiardsstats.write.user.UserCreatedEvent
import org.axonframework.eventhandling.EventHandler
import org.springframework.stereotype.Component

/**
 * @author: Knut Esten Melandsø Nekså
 */
@Component
open class UserEventHandler(private val userDao: UserDao) {
    @EventHandler
    fun onUserCreated(userCreatedEvent: UserCreatedEvent) {
        userDao.create(
                User(userCreatedEvent.id,
                        userCreatedEvent.email,
                        userCreatedEvent.givenName,
                        userCreatedEvent.familyName))
    }
}