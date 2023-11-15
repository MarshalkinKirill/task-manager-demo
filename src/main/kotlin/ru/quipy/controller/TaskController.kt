package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.*
import ru.quipy.core.EventSourcingService
import ru.quipy.logic.*
import java.lang.IllegalArgumentException
import java.util.*

@RestController
@RequestMapping("/tasks")
class TaskController(
        val taskEsService: EventSourcingService<UUID, TaskAggregate, TaskAggregateState>,
){
    @PostMapping("/{projectId}/task")
    fun createTask(@PathVariable projectId: UUID, @RequestParam taskName: String) : TaskCreatedEvent {
        return taskEsService.create { it.createTask(projectId, UUID.randomUUID(), taskName) }
    }

    @GetMapping("/{taskId}")
    fun getTask (@PathVariable taskId: UUID): TaskAggregateState? {
        return taskEsService.getState(taskId)
    }

    @PostMapping("/{taskId}/changeTask/")
    fun changeTaskTitle(@PathVariable projectId: UUID, @PathVariable taskId: UUID, @RequestParam newName: String) : TaskNameChangeEvent? {
        return taskEsService.update(taskId){ it.changeTaskTitle(taskId, newName) }
    }

    @PostMapping("/{taskId}/addUser/{userId}")
    fun addExecutorToTask(@PathVariable taskId: UUID, @RequestParam userId: UUID) : AssignedExecutorToTaskEvent?{
        return taskEsService.update(taskId){ it.addExecutorToTask(userId, taskId) }
    }


}