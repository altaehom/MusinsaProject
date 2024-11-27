package com.musinsa.project.application.outbox

import com.musinsa.project.application.ranking.event.RankingEventConverter
import com.musinsa.project.application.ranking.event.RankingEventType
import com.musinsa.project.domain.service.outbox.OutboxDomainService
import com.musinsa.project.infra.event.DomainEventPublisher
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * 도메인 서비스에서 사출 된 도메인 이벤트를 전송하는 클래스
 */
@Component
class OutboxEventSender(
    private val outboxDomainService: OutboxDomainService,
    private val domainEventPublisher: DomainEventPublisher,
    private val rankingEventConverter: RankingEventConverter,
    private val eventDuplicateChecker: EventDuplicateChecker,
) {
    @Transactional
    fun send(id: Long) {
        val data = outboxDomainService.getOne(id) ?: return
        if (data.payload.isBlank()) throw IllegalStateException("payload is blank")
        if (eventDuplicateChecker.isDuplicate(data.eventId)) return

        val eventType = RankingEventType.getType(data.eventType)

        if (eventType.isUnknownEvent()) {
            outboxDomainService.markPublish(data)
            return
        }

        val rankingEvent =
            rankingEventConverter.convertEvent(
                type = eventType,
                payload = data.payload,
            )

        runCatching {
            domainEventPublisher.publish(rankingEvent)
        }.onSuccess {
            outboxDomainService.markPublish(data)
            eventDuplicateChecker.mark(data.eventId)
        }.onFailure { log.error("Send Error!", it) }
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}
