package com.musinsa.project.application.ranking.model

import java.math.BigDecimal

data class CategoryPriceRankModel(
    val categoryId: Long,
    val brandId: Long?,
    val price: BigDecimal?,
)
