package ru.quipy.logic

import ru.quipy.api.*
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

// Service's business logic
class ProjectAggregateState : AggregateState<UUID, ProjectAggregate> {
    private lateinit var projectId: UUID
    var createdAt: Long = System.currentTimeMillis()
    var updatedAt: Long = System.currentTimeMillis()

    lateinit var projectTitle: String
    lateinit var creatorId: String
    var tasks = mutableMapOf<UUID, TaskEntity>()
    var projectTags = mutableMapOf<UUID, TagEntity>()

    override fun getId() = projectId

    // State transition functions which is represented by the class member function
    @StateTransitionFunc
    fun projectCreatedApply(event: ProjectCreatedEvent) {
        projectId = event.projectId
        projectTitle = event.title
        creatorId = event.creatorId
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun tagCreatedApply(event: TagCreatedEvent) {
        projectTags[event.tagId] = TagEntity(event.tagId, event.tagName)
        updatedAt = createdAt
    }

    @StateTransitionFunc
    fun taskCreatedApply(event: TaskCreatedEvent) {
        tasks[event.taskId] = TaskEntity(event.taskId, event.taskName, mutableSetOf())
        updatedAt = createdAt
    }

    fun create(id: UUID, title: String, creatorId: String): ProjectCreatedEvent {
        return ProjectCreatedEvent(
                projectId = id,
                title = title,
                creatorId = creatorId,
        )
    }

    fun addTask(name: String): TaskCreatedEvent {
        return TaskCreatedEvent(projectId = this.getId(), taskId = UUID.randomUUID(), taskName = name)
    }

    fun createTag(name: String): TagCreatedEvent {
        if (projectTags.values.any { it.name == name }) {
            throw IllegalArgumentException("Tag already exists: $name")
        }
        return TagCreatedEvent(projectId = this.getId(), tagId = UUID.randomUUID(), tagName = name)
    }

    fun assignTagToTask(tagId: UUID, taskId: UUID): TagAssignedToTaskEvent {
        if (!projectTags.containsKey(tagId)) {
            throw IllegalArgumentException("Tag doesn't exists: $tagId")
        }

        if (!tasks.containsKey(taskId)) {
            throw IllegalArgumentException("Task doesn't exists: $taskId")
        }

        return TagAssignedToTaskEvent(projectId = this.getId(), tagId = tagId, taskId = taskId)
    }
}

data class TaskEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val tagsAssigned: MutableSet<UUID>
)

data class TagEntity(
    val id: UUID = UUID.randomUUID(),
    val name: String
)

/**
 * Demonstrates that the transition functions might be representer by "extension" functions, not only class members functions
 */
@StateTransitionFunc
fun ProjectAggregateState.tagAssignedApply(event: TagAssignedToTaskEvent) {
    tasks[event.taskId]?.tagsAssigned?.add(event.tagId)
        ?: throw IllegalArgumentException("No such task: ${event.taskId}")
    updatedAt = createdAt
}
