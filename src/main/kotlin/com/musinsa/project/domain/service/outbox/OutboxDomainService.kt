package com.musinsa.project.domain.service.outbox

import com.fasterxml.jackson.databind.ObjectMapper
import com.musinsa.project.domain.entity.outbox.Outbox
import com.musinsa.project.domain.entity.outbox.OutboxRepository
import com.musinsa.project.infra.event.Events.OutboxEvent
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class OutboxDomainService(
    private val outboxRepository: OutboxRepository,
    private val objectMapper: ObjectMapper,
) {
    fun getOne(id: Long) = outboxRepository.findByIdOrNull(id)

    fun getNotPublished() = outboxRepository.findByPublishedIsFalseOrderByIdDescLimit100()

    fun save(event: OutboxEvent) {
        val target =
            Outbox(
                eventType = event.eventType,
                aggregateType = event.aggregateType,
                header = objectMapper.writeValueAsString(event.header),
                payload = objectMapper.writeValueAsString(event.event),
                eventId = event.eventId.toString(),
            )
        outboxRepository.save(target)
    }

    fun markPublish(outbox: Outbox) {
        outbox.published()
    }
}
