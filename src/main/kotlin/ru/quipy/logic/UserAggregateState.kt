package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class UserAggregateState : AggregateState<UUID, UserAggregate> {
    private lateinit var userId: UUID
    lateinit var login: String
    lateinit var password: String
    override fun getId() = userId

    @StateTransitionFunc
    fun createUser(event: UserCreatedEvent) {
        userId = this.getId()
        login = event.login
        password = event.password
    }

    fun createUser(userId: UUID,
                   login: String,
                   password: String): UserCreatedEvent{
        return  UserCreatedEvent(
                userId = userId,
                login = login,
                password = password
        )
    }
}