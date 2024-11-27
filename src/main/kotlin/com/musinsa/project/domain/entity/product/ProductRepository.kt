package com.musinsa.project.domain.entity.product

import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface ProductRepository : JpaRepository<Product, Long> {
    fun findByBrandId(brandId: Long): List<Product>

    @Profile("test")
    @Modifying
    @Query(
        """
            DELETE FROM PRODUCT WHERE id > 0
        """,
        nativeQuery = true,
    )
    fun clear()
}
