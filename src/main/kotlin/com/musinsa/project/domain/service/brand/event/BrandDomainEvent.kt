package com.musinsa.project.domain.service.brand.event

import com.musinsa.project.infra.event.DomainEvent
import java.time.Instant

sealed class BrandDomainEvent : DomainEvent {
    abstract val id: Long

    data class BrandCreatedEvent(
        override val id: Long,
        val brandName: String,
        val createdAt: Instant,
        val updatedAt: Instant?,
    ) : BrandDomainEvent() {
        override val key: String = id.toString()
        override val aggregateType: String = AGG_NAME
    }

    data class BrandUpdatedEvent(
        override val id: Long,
        val brandName: String,
        val createdAt: Instant,
        val updatedAt: Instant?,
    ) : BrandDomainEvent() {
        override val key: String = id.toString()
        override val aggregateType: String = AGG_NAME
    }

    data class BrandDeletedEvent(
        override val id: Long,
    ) : BrandDomainEvent() {
        override val key: String = id.toString()
        override val aggregateType: String = AGG_NAME
    }

    companion object {
        private const val AGG_NAME = "BRAND"
    }
}
