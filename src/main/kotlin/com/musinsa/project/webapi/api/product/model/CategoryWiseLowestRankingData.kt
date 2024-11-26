package com.musinsa.project.webapi.api.product.model

import com.fasterxml.jackson.annotation.JsonProperty

data class CategoryWiseLowestRankingData(
    @JsonProperty("카테고리")
    val categoryName: String,
    @JsonProperty("브랜드")
    val brandName: String?,
    @JsonProperty("가격")
    val price: String?,
)

data class CategoryWiseLowestRankingResponse(
    val items: Collection<CategoryWiseLowestRankingData>,
    @JsonProperty("총액")
    val totalPrice: String,
)
