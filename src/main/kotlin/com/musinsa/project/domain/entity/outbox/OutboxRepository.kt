package com.musinsa.project.domain.entity.outbox

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface OutboxRepository : JpaRepository<Outbox, Long> {
    @Query(
        """
            SELECT s FROM Outbox s WHERE s.published = FALSE ORDER BY s.id ASC LIMIT 100
        """,
    )
    fun findByPublishedIsFalseOrderByIdDescLimit100(): List<Outbox>
}
