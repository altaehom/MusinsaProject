package com.musinsa.project.domain.entity.brand

import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface BrandRepository : JpaRepository<Brand, Long> {
    @Profile("test")
    @Modifying
    @Query(
        """
            DELETE FROM BRAND WHERE id > 0
        """,
        nativeQuery = true,
    )
    fun clear()

    fun findByBrandName(brandName: String): List<Brand>
}
