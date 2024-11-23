package com.musinsa.project.domain.service.category.model

import com.musinsa.project.domain.entity.category.Category
import java.time.Instant

data class CategoryModel private constructor(
    val id: Long,
    val categoryName: String,
    val createdAt: Instant,
) {
    companion object {
        operator fun invoke(item: Category): CategoryModel =
            with(item) {
                CategoryModel(
                    id = id!!,
                    categoryName = categoryName,
                    createdAt = createdAt,
                )
            }
    }
}
