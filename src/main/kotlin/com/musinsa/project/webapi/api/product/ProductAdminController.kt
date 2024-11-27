package com.musinsa.project.webapi.api.product

import com.musinsa.project.application.product.ProductAdminService
import com.musinsa.project.webapi.api.product.model.CreateProductAdminRequest
import com.musinsa.project.webapi.api.product.model.UpdateProductAdminRequest
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/product")
class ProductAdminController(
    private val productAdminService: ProductAdminService,
) {
    @PostMapping
    fun createProduct(
        @RequestBody request: CreateProductAdminRequest,
    ) {
        with(request) {
            productAdminService.createProduct(
                brandId = brandId,
                categoryId = categoryId,
                price = price,
            )
        }
    }

    @PutMapping(path = ["/{id}"])
    fun updateProduct(
        @PathVariable id: Long,
        @RequestBody request: UpdateProductAdminRequest,
    ) {
        productAdminService.updateProduct(
            id = id,
            price = request.price,
        )
    }

    @DeleteMapping(path = ["/{id}"])
    fun deleteProduct(
        @PathVariable id: Long,
    ) {
        productAdminService.removeProduct(id)
    }
}
