package com.musinsa.project.domain.service.brand

import com.musinsa.project.domain.entity.brand.Brand
import com.musinsa.project.domain.entity.brand.BrandRepository
import com.musinsa.project.domain.exception.DomainException.DomainNotFoundException
import com.musinsa.project.domain.service.brand.model.BrandModel
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class BrandDomainService(
    private val brandRepository: BrandRepository,
) {
    @Transactional
    fun save(brandName: String) {
        brandRepository.save(Brand(brandName))
    }

    fun get(id: Long) =
        brandRepository
            .findByIdOrNull(id)
            ?.let(BrandModel::invoke)

    private fun getEntity(id: Long) = brandRepository.findByIdOrNull(id)

    @Transactional
    fun update(
        id: Long,
        brandName: String,
    ) {
        getEntity(id)
            ?.modify(brandName)
            ?: throw DomainNotFoundException(id)
    }

    @Transactional
    fun delete(id: Long) {
        getEntity(id)
            ?.remove()
            ?: throw DomainNotFoundException(id)
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}
