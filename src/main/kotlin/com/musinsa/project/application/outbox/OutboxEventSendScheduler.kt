package com.musinsa.project.application.outbox

import com.musinsa.project.domain.service.outbox.OutboxDomainService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
* 도메인 서비스에서 사출 된 도메인 이벤트를 주기적으로 조회 하는 스케쥴러
*/
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
