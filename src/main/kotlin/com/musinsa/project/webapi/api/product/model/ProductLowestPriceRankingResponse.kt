package com.musinsa.project.webapi.api.product.model

import com.fasterxml.jackson.annotation.JsonProperty

data class ProductLowestPriceRankingResponse(
    @JsonProperty("카테고리")
    val categoryName: String,
    @JsonProperty("브랜드")
    val brandName: String?,
    @JsonProperty("가격")
    val price: String?,
)

data class ProductLowestPriceRankingResponses(
    val items: Collection<ProductLowestPriceRankingResponse>,
    @JsonProperty("총액")
    val totalPrice: String,
)
