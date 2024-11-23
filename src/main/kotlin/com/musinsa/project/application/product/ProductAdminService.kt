package com.musinsa.project.application.product

import com.musinsa.project.application.exception.ApplicationException.BrandNotFoundException
import com.musinsa.project.application.exception.ApplicationException.CategoryNotFoundException
import com.musinsa.project.application.exception.ApplicationException.ProductNotFoundException
import com.musinsa.project.application.exception.ApplicationException.ProductPriceException
import com.musinsa.project.domain.service.brand.BrandDomainService
import com.musinsa.project.domain.service.category.CategoryDomainService
import com.musinsa.project.domain.service.product.ProductDomainService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional(readOnly = true)
class ProductAdminService(
    private val brandDomainService: BrandDomainService,
    private val categoryDomainService: CategoryDomainService,
    private val productDomainService: ProductDomainService,
) {
    @Transactional
    fun createProduct(
        brandId: Long,
        categoryId: Long,
        price: BigDecimal,
    ) {
        if (price <= BigDecimal.ZERO) throw ProductPriceException()

        getCategoryModel(categoryId)
        getBrandModel(brandId)

        productDomainService.save(
            brandId = brandId,
            categoryId = categoryId,
            price = price,
        )
    }

    @Transactional
    fun updateProduct(
        id: Long,
        price: BigDecimal,
    ) {
        if (price <= BigDecimal.ZERO) throw ProductPriceException()

        getProductModel(id)

        productDomainService.update(
            id = id,
            price = price,
        )
    }

    @Transactional
    fun removeProduct(id: Long) {
        getProductModel(id)
        productDomainService.delete(id)
    }

    private fun getBrandModel(id: Long) = brandDomainService.get(id) ?: throw BrandNotFoundException()

    private fun getCategoryModel(id: Long) = categoryDomainService.get(id) ?: throw CategoryNotFoundException()

    private fun getProductModel(id: Long) = productDomainService.get(id) ?: throw ProductNotFoundException()
}
