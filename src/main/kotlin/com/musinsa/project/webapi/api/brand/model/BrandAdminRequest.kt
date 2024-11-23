package com.musinsa.project.webapi.api.brand.model

data class CreateBrandAdminRequest(
    val brandName: String,
) {
    init {
        require(brandName.isNotBlank()) {
            "Param Is Blank"
        }
    }
}

data class UpdateBrandAdminRequest(
    val brandName: String,
) {
    init {
        require(brandName.isNotBlank()) {
            "Param Is Blank"
        }
    }
}
