package com.musinsa.project.domain.service

import com.musinsa.project.domain.service.outbox.OutboxDomainService
import com.musinsa.project.infra.event.CloudEvent
import com.musinsa.project.infra.event.DomainEvent
import com.musinsa.project.infra.event.Headers
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class DomainEventListener(
    private val outboxDomainService: OutboxDomainService,
) {
    @EventListener
    @Transactional
    fun handler(event: DomainEvent) {
        val cloudEvent =
            CloudEvent(
                event = event,
                eventType = event::class.simpleName.toString(),
                aggregateType = event.aggregateType,
                header = Headers(event.key),
            )
        outboxDomainService.save(cloudEvent)
    }
}