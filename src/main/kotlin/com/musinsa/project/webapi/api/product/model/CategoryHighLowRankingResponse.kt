package com.musinsa.project.webapi.api.product.model

import com.fasterxml.jackson.annotation.JsonProperty

data class CategoryHighLowRankingResponse(
    @JsonProperty("카테고리")
    val categoryName: String,
    @JsonProperty("최저가")
    val lowest: BrandResponse?,
    @JsonProperty("최고가")
    val highest: BrandResponse?,
)
