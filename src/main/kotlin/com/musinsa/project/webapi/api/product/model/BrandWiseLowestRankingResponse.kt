package com.musinsa.project.webapi.api.product.model

import com.fasterxml.jackson.annotation.JsonProperty

data class BrandWiseLowestRankingData(
    @JsonProperty("카테고리")
    val categories: List<CategoryResponse>,
    @JsonProperty("브랜드")
    val brandName: String?,
    @JsonProperty("총액")
    val totalPrice: String,
)

data class BrandWiseLowestRankingResponse(
    @JsonProperty("최저가")
    val item: BrandWiseLowestRankingData,
)
