package com.musinsa.project.application.brand

import com.musinsa.project.application.exception.ApplicationException.CommonException
import com.musinsa.project.application.exception.ApplicationException.NotFoundException
import com.musinsa.project.domain.service.brand.BrandDomainService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class BrandAdminService(
    private val brandDomainService: BrandDomainService,
) {
    @Transactional
    fun createBrand(brandName: String) {
        if (brandName.isBlank()) throw CommonException("BrandName is blank")

        brandDomainService.save(brandName)
    }

    @Transactional
    fun updateBrand(
        id: Long,
        brandName: String,
    ) {
        if (brandName.isBlank()) throw CommonException("BrandName is blank")

        getModel(id)
        brandDomainService.update(
            id = id,
            brandName = brandName,
        )
    }

    @Transactional
    fun removeBrand(id: Long) {
        val brand = getModel(id)
        brandDomainService.delete(brand.id)
    }

    private fun getModel(id: Long) = brandDomainService.get(id) ?: throw NotFoundException()
}
