package com.musinsa.project.domain.service.product.event

import com.musinsa.project.infra.event.DomainEvent
import java.math.BigDecimal
import java.time.Instant

sealed class ProductDomainEvent : DomainEvent {
    abstract val id: Long
    abstract val brandId: Long
    abstract val categoryId: Long
    abstract val price: BigDecimal

    data class ProductCreatedEvent(
        override val id: Long,
        override val brandId: Long,
        override val categoryId: Long,
        override val price: BigDecimal,
        val createdAt: Instant,
        val updatedAt: Instant?,
    ) : ProductDomainEvent() {
        override val key: String = id.toString()
        override val aggregateType: String = AGG_NAME
    }

    data class ProductUpdatedEvent(
        override val id: Long,
        override val brandId: Long,
        override val categoryId: Long,
        override val price: BigDecimal,
        val beforePrice: BigDecimal,
        val createdAt: Instant,
        val updatedAt: Instant?,
    ) : ProductDomainEvent() {
        override val key: String = id.toString()
        override val aggregateType: String = AGG_NAME
    }

    data class ProductDeletedEvent(
        override val id: Long,
        override val brandId: Long,
        override val categoryId: Long,
        override val price: BigDecimal,
    ) : ProductDomainEvent() {
        override val key: String = id.toString()
        override val aggregateType: String = AGG_NAME
    }

    companion object {
        private const val AGG_NAME = "PRODUCT"
    }
}
