package com.musinsa.project.webapi.api.product.model

import java.math.BigDecimal

data class CreateProductAdminRequest(
    val brandId: Long,
    val categoryId: Long,
    val price: BigDecimal,
) {
    init {
        require(price > BigDecimal.ZERO) {
            "Price is a negative quantity"
        }
    }
}

data class UpdateProductAdminRequest(
    val price: BigDecimal,
) {
    init {
        require(price > BigDecimal.ZERO) {
            "Price is a negative quantity"
        }
    }
}
