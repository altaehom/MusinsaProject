package com.musinsa.project.infra.event

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.Instant
import java.util.UUID

sealed interface Events {
    val eventId: UUID
    val version: String
    val eventType: String
    val aggregateType: String
    val sendAt: Instant
    val event: Any

    interface OutboxEvent : Events {
        val header: Headers
    }
}

interface DomainEvent {
    @get:JsonIgnore
    val key: String

    @get:JsonIgnore
    val aggregateType: String
}
