package com.musinsa.project.domain.entity.category

import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface CategoryRepository : JpaRepository<Category, Long> {
    @Profile("test")
    @Modifying
    @Query(
        """
            DELETE FROM CATEGORY WHERE id > 0
        """,
        nativeQuery = true,
    )
    fun clear()

    fun findByCategoryName(categoryName: String): List<Category>
}
