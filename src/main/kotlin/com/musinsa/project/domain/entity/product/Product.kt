package com.musinsa.project.domain.entity.product

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Table
import org.hibernate.annotations.DynamicUpdate
import jakarta.persistence.Id
import java.math.BigDecimal
import java.time.Instant

@Entity
@DynamicUpdate
@Table(name = "product")
class Product private constructor(
    val createdAt: Instant = Instant.now(),
    val brandId: Long,
    val categoryId: Long,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var price: BigDecimal = BigDecimal.ZERO
    var deleted: Boolean = false
    var updatedAt: Instant? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Product

        if (createdAt != other.createdAt) return false
        if (brandId != other.brandId) return false
        if (categoryId != other.categoryId) return false
        if (id != other.id) return false
        if (price != other.price) return false
        if (deleted != other.deleted) return false
        if (updatedAt != other.updatedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = createdAt.hashCode()
        result = 31 * result + brandId.hashCode()
        result = 31 * result + categoryId.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + price.hashCode()
        result = 31 * result + deleted.hashCode()
        result = 31 * result + (updatedAt?.hashCode() ?: 0)
        return result
    }
}
