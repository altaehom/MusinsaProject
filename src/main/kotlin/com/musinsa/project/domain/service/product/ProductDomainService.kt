package com.musinsa.project.domain.service.product

import com.musinsa.project.domain.entity.product.Product
import com.musinsa.project.domain.entity.product.ProductRepository
import com.musinsa.project.domain.exception.DomainException.DomainNotFoundException
import com.musinsa.project.domain.service.product.model.ProductModel
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional(readOnly = true)
class ProductDomainService(
    private val productRepository: ProductRepository,
) {
    @Transactional
    fun save(
        brandId: Long,
        categoryId: Long,
        price: BigDecimal,
    ) {
        val product =
            Product(
                brandId = brandId,
                categoryId = categoryId,
                price = price,
            )
        productRepository.save(product)
    }

    fun get(id: Long) =
        productRepository
            .findByIdOrNull(id)
            ?.let(ProductModel::invoke)

    private fun getEntity(id: Long) = productRepository.findByIdOrNull(id)

    @Transactional
    fun update(
        id: Long,
        price: BigDecimal,
    ) {
        getEntity(id)
            ?.modify(price)
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
