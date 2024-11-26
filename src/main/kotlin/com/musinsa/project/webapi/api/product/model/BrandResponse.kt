package com.musinsa.project.webapi.api.product.model

import com.fasterxml.jackson.annotation.JsonProperty

data class BrandResponse(
    @JsonProperty("브랜드")
    val brandName: String,
    @JsonProperty("가격")
    val price: String,
)
