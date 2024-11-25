package com.musinsa.project.webapi.api.product.model

import com.fasterxml.jackson.annotation.JsonProperty

data class CategoryResponse(
    @JsonProperty("카테고리")
    val categoryName: String,
    @JsonProperty("가격")
    val price: String,
)
