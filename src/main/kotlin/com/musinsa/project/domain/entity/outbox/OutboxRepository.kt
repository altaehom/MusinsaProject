package com.musinsa.project.domain.entity.outbox

import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface OutboxRepository : JpaRepository<Outbox, Long> {
    @Query(
        """
            SELECT s FROM Outbox s WHERE s.published = FALSE ORDER BY s.id ASC LIMIT 100
        """,
    )
    fun findByPublishedIsFalseOrderByIdDescLimit100(): List<Outbox>

    @Profile("test")
    @Modifying
    @Query(
        """
            DELETE FROM OUTBOX WHERE id > 0
        """,
        nativeQuery = true,
    )
    fun clear()
}
