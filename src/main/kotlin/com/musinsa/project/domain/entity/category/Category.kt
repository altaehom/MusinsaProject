package com.musinsa.project.domain.entity.category

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
@Table(name = "category")
@SQLRestriction("deleted = false")
class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var categoryName: String = ""
    var deleted: Boolean = false
    val createdAt: Instant = Instant.now()
    var updatedAt: Instant? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Category

        if (createdAt != other.createdAt) return false
        if (id != other.id) return false
        if (categoryName != other.categoryName) return false
        if (deleted != other.deleted) return false
        if (updatedAt != other.updatedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = createdAt.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + categoryName.hashCode()
        result = 31 * result + deleted.hashCode()
        result = 31 * result + (updatedAt?.hashCode() ?: 0)
        return result
    }

    companion object {
        operator fun invoke(categoryName: String) = Category().apply { this.categoryName = categoryName }
    }
}
