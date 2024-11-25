package com.musinsa.project.application.outbox

import com.musinsa.project.domain.service.outbox.OutboxDomainService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class OutboxEventSendScheduler(
    private val outboxDomainService: OutboxDomainService,
    private val outboxEventSender: OutboxEventSender,
) {
    @Scheduled(fixedDelay = 1000)
    fun scheduled() {
        outboxDomainService
            .getNotPublished()
            .forEach {
                outboxEventSender.send(it.id!!)
            }
    }
}
