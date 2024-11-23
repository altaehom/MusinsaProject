package com.musinsa.project.webapi.api.brand

import com.musinsa.project.application.brand.BrandAdminService
import com.musinsa.project.webapi.api.brand.model.CreateBrandAdminRequest
import com.musinsa.project.webapi.api.brand.model.UpdateBrandAdminRequest
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/brand")
class BrandAdminController(
    private val brandAdminService: BrandAdminService,
) {
    @PostMapping
    fun createBrand(
        @RequestBody request: CreateBrandAdminRequest,
    ) {
        brandAdminService.createBrand(request.brandName)
    }

    @PutMapping(path = ["/{id}"])
    fun updateBrand(
        @PathVariable id: Long,
        @RequestBody request: UpdateBrandAdminRequest,
    ) {
        brandAdminService.updateBrand(
            id = id,
            brandName = request.brandName,
        )
    }

    @DeleteMapping(path = ["/{id}"])
    fun deleteBrand(
        @PathVariable id: Long,
    ) {
        brandAdminService.removeBrand(id)
    }
}
