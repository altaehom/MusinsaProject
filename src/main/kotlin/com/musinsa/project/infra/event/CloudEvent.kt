package com.musinsa.project.infra.event

import com.musinsa.project.infra.event.Events.OutboxEvent
import java.time.Instant
import java.util.UUID

data class CloudEvent private constructor(
    override val event: Any,
    override val eventType: String,
    override val aggregateType: String,
    override val header: Headers,
) : OutboxEvent {
    override val version: String = "1.0"
    override val eventId: UUID = UUID.randomUUID()
    override val sendAt: Instant = Instant.now()

    companion object {
        operator fun invoke(
            event: Any,
            eventType: String,
            aggregateType: String,
            header: Headers,
        ): CloudEvent =
            CloudEvent(
                event = event,
                eventType = eventType,
                aggregateType = aggregateType,
                header = header,
            )
    }
}
