package com.musinsa.project.infra.event

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

@Component
class DomainEventPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher,
) {
    fun publish(event: Any) {
        applicationEventPublisher.publishEvent(event)
    }
}
