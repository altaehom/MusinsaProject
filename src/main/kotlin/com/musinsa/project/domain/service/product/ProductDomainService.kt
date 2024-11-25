package com.musinsa.project.domain.service.product

import com.musinsa.project.domain.entity.product.Product
import com.musinsa.project.domain.entity.product.ProductRepository
import com.musinsa.project.domain.exception.DomainException.DomainNotFoundException
import com.musinsa.project.domain.service.product.event.ProductDomainEvent.ProductCreatedEvent
import com.musinsa.project.domain.service.product.event.ProductDomainEvent.ProductDeletedEvent
import com.musinsa.project.domain.service.product.event.ProductDomainEvent.ProductRemovedEvent
import com.musinsa.project.domain.service.product.event.ProductDomainEvent.ProductUpdatedEvent
import com.musinsa.project.domain.service.product.model.ProductModel
import com.musinsa.project.infra.event.DomainEventPublisher
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional(readOnly = true)
class ProductDomainService(
    private val productRepository: ProductRepository,
    private val domainEventPublisher: DomainEventPublisher,
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
        productRepository
            .save(product)
            .also {
                domainEventPublisher.publish(
                    ProductCreatedEvent(
                        id = it.id ?: 0,
                        brandId = it.brandId,
                        categoryId = it.categoryId,
                        price = it.price,
                        createdAt = it.createdAt,
                        updatedAt = it.updatedAt,
                    ),
                )
            }
    }

    fun get(id: Long) =
        productRepository
            .findByIdOrNull(id)
            ?.let(ProductModel::invoke)

    fun getByBrandId(brandId: Long) =
        productRepository
            .findByBrandId(brandId)
            .map(ProductModel::invoke)

    private fun getEntity(id: Long) = productRepository.findByIdOrNull(id)

    @Transactional
    fun update(
        id: Long,
        price: BigDecimal,
    ) {
        val product = getEntity(id) ?: throw DomainNotFoundException(id)
        val beforePrice = BigDecimal(product.price.toString())

        product.modify(price)
        domainEventPublisher.publish(
            ProductUpdatedEvent(
                id = id,
                brandId = product.brandId,
                categoryId = product.categoryId,
                beforePrice = beforePrice,
                price = product.price,
                createdAt = product.createdAt,
                updatedAt = product.updatedAt,
            ),
        )
    }

    @Transactional
    fun delete(id: Long) {
        getEntity(id)
            ?.remove()
            ?.also {
                domainEventPublisher.publish(
                    ProductRemovedEvent(
                        id = it.id ?: 0,
                        brandId = it.brandId,
                        categoryId = it.categoryId,
                        price = it.price,
                    ),
                )
            }
            ?: throw DomainNotFoundException(id)
    }

    @Transactional
    fun deleteByBrandId(brandId: Long) {
        productRepository
            .findByBrandId(brandId)
            .onEach { it.remove() }
            .forEach {
                domainEventPublisher.publish(
                    ProductDeletedEvent(
                        id = it.id ?: 0,
                        brandId = it.brandId,
                        categoryId = it.categoryId,
                        price = it.price,
                    ),
                )
            }
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}
