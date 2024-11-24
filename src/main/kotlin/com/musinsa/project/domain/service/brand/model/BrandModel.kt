package com.musinsa.project.domain.service.brand.model

import com.musinsa.project.domain.entity.brand.Brand
import java.time.Instant

data class BrandModel(
    val id: Long,
    val brandName: String,
    val createAt: Instant,
    val updateAt: Instant?,
) {
    companion object {
        operator fun invoke(item: Brand): BrandModel =
            with(item) {
                BrandModel(
                    id = id!!,
                    brandName = brandName,
                    createAt = createdAt,
                    updateAt = updatedAt,
                )
            }
    }
}
