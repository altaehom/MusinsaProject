package com.musinsa.project.domain.entity.brand

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.SQLRestriction
import java.time.Instant

@Entity
@DynamicUpdate
@Table(name = "brand")
@SQLRestriction("deleted = false")
class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var brandName: String = ""
    var deleted: Boolean = false
    val createdAt: Instant = Instant.now()
    var updatedAt: Instant? = null

    private fun create(brandName: String) =
        apply {
            this.brandName = brandName
        }

    fun modify(brandName: String) =
        apply {
            this.brandName = brandName
            this.updatedAt = Instant.now()
        }

    fun remove() =
        apply {
            this.deleted = true
            this.updatedAt = Instant.now()
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Brand

        if (createdAt != other.createdAt) return false
        if (id != other.id) return false
        if (brandName != other.brandName) return false
        if (deleted != other.deleted) return false
        if (updatedAt != other.updatedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = createdAt.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + brandName.hashCode()
        result = 31 * result + deleted.hashCode()
        result = 31 * result + (updatedAt?.hashCode() ?: 0)
        return result
    }

    companion object {
        operator fun invoke(brandName: String): Brand = Brand().create(brandName)
    }
}
