package com.musinsa.project.domain.entity.board

import jakarta.persistence.*
import org.hibernate.annotations.DynamicUpdate
import java.time.Instant

@Entity
@DynamicUpdate
@Table(name = "board")
class Board private constructor(
    val createdAt: Instant = Instant.now(),
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var title: String = ""
    var body: String? = null

    var updatedAt: Instant? = null

    private fun create(
        title: String,
        body: String?,
    ) = apply {
        this.title = title
        this.body = body
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as com.musinsa.project.domain.entity.board.Board

        if (createdAt != other.createdAt) return false
        if (id != other.id) return false
        if (title != other.title) return false
        if (body != other.body) return false
        if (updatedAt != other.updatedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = createdAt.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + title.hashCode()
        result = 31 * result + (body?.hashCode() ?: 0)
        result = 31 * result + (updatedAt?.hashCode() ?: 0)
        return result
    }

    companion object {
        operator fun invoke(
            title: String,
            body: String?,
        ): com.musinsa.project.domain.entity.board.Board =
            com.musinsa.project.domain.entity.board.Board().create(
                title = title,
                body = body,
            )
    }
}
