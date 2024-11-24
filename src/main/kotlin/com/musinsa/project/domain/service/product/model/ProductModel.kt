package com.musinsa.project.domain.service.product.model

import com.musinsa.project.domain.entity.product.Product
import java.math.BigDecimal
import java.time.Instant

data class ProductModel(
    val id: Long,
    val brandId: Long,
    val categoryId: Long,
    val price: BigDecimal,
    val createAt: Instant,
    val updateAt: Instant?,
) {
    companion object {
        operator fun invoke(item: Product): ProductModel =
            with(item) {
                ProductModel(
                    id = id!!,
                    brandId = brandId,
                    categoryId = categoryId,
                    price = price,
                    createAt = createdAt,
                    updateAt = updatedAt,
                )
            }
    }
}
