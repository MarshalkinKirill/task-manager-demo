package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.lang.IllegalArgumentException
import java.util.*

@RestController
@RequestMapping("/users")
class UserController(
        val userEsService: EventSourcingService<UUID, UserAggregate, UserAggregateState>,
) {
    @PostMapping("/{userName}")
    fun createUser(@PathVariable userName: String, @RequestParam password: String) : UserCreatedEvent {
        return userEsService.create { it.createUser(UUID.randomUUID(), userName, password) }
    }

    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: UUID) : UserAggregateState? {
        return userEsService.getState(userId)
    }
}