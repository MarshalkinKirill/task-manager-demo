package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.UUID

class TaskAggregateState : AggregateState<UUID, TaskAggregate>{
    private lateinit var taskId: UUID
    lateinit var taskName: String
    lateinit var statusId: UUID
    lateinit var projectId: UUID

    var executors = mutableSetOf<UUID>()
    override fun getId() = taskId

    @StateTransitionFunc
    fun createTask(event: TaskCreatedEvent){
        taskId = event.taskId
        projectId = event.projectId
        taskName = event.taskName
    }

    @StateTransitionFunc
    fun changeTaskTitle(event: TaskNameChangeEvent){
        taskName = event.newTaskName
    }

    @StateTransitionFunc
    fun changeTaskStatus(event: TaskStatusChangeEvent){
        statusId = event.newStatusId
    }

    @StateTransitionFunc
    fun addExecutorToTask(event: AssignedExecutorToTaskEvent){
        executors.add(event.userId)
    }

    fun createTask(projectId: UUID, taskId: UUID, taskName: String): TaskCreatedEvent{
        return  TaskCreatedEvent(
                projectId = projectId,
                taskId = taskId,
                taskName = taskName
        )
    }

    fun changeTaskTitle(taskId: UUID, newTaskName: String) : TaskNameChangeEvent {
        return TaskNameChangeEvent(
                taskId = taskId,
                oldTaskName = this.taskName,
                newTaskName = newTaskName
        )
    }

    fun addExecutorToTask(taskId: UUID, userId: UUID) : AssignedExecutorToTaskEvent {
        return AssignedExecutorToTaskEvent(
                taskId = taskId,
                userId = userId
        )
    }

    fun changeTaskStatus(statusId: UUID): TaskStatusChangeEvent {
        return TaskStatusChangeEvent(
                taskId = this.getId(),
                newStatusId = statusId,
                oldStatusId = this.statusId
        )
    }
}