package com.musinsa.project.domain.entity.outbox

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import java.time.Instant

@Entity
@Table(name = "outbox")
class Outbox private constructor(
    val eventId: String,
    val eventType: String,
    val aggregateType: String,
    val header: String,
    val payload: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var published = false

    @CreatedDate
    val createdAt: Instant = Instant.now()
    var processedAt: Instant? = null

    fun published() =
        apply {
            published = true
            processedAt = Instant.now()
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Outbox

        if (eventType != other.eventType) return false
        if (aggregateType != other.aggregateType) return false
        if (header != other.header) return false
        if (payload != other.payload) return false
        if (createdAt != other.createdAt) return false
        if (id != other.id) return false
        if (published != other.published) return false
        if (processedAt != other.processedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = eventType.hashCode()
        result = 31 * result + aggregateType.hashCode()
        result = 31 * result + header.hashCode()
        result = 31 * result + payload.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + published.hashCode()
        result = 31 * result + (processedAt?.hashCode() ?: 0)
        return result
    }

    companion object {
        operator fun invoke(
            eventType: String,
            aggregateType: String,
            header: String,
            payload: String,
            eventId: String,
        ): Outbox =
            Outbox(
                eventId = eventId,
                eventType = eventType,
                aggregateType = aggregateType,
                header = header,
                payload = payload,
            )
    }
}
