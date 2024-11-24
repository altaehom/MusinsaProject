package com.musinsa.project.domain.service.brand

import com.musinsa.project.domain.entity.brand.Brand
import com.musinsa.project.domain.entity.brand.BrandRepository
import com.musinsa.project.domain.exception.DomainException.DomainNotFoundException
import com.musinsa.project.domain.service.brand.event.BrandDomainEvent.BrandCreatedEvent
import com.musinsa.project.domain.service.brand.event.BrandDomainEvent.BrandDeletedEvent
import com.musinsa.project.domain.service.brand.event.BrandDomainEvent.BrandUpdatedEvent
import com.musinsa.project.domain.service.brand.model.BrandModel
import com.musinsa.project.infra.event.DomainEventPublisher
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class BrandDomainService(
    private val brandRepository: BrandRepository,
    private val domainEventPublisher: DomainEventPublisher,
) {
    @Transactional
    fun save(brandName: String) {
        brandRepository
            .save(Brand(brandName))
            .also {
                domainEventPublisher.publish(
                    BrandCreatedEvent(
                        id = it.id ?: 0,
                        brandName = it.brandName,
                        createdAt = it.createdAt,
                        updatedAt = it.updatedAt,
                    ),
                )
            }
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
            ?.also {
                domainEventPublisher.publish(
                    BrandUpdatedEvent(
                        id = id,
                        brandName = it.brandName,
                        createdAt = it.createdAt,
                        updatedAt = it.updatedAt,
                    ),
                )
            }
            ?: throw DomainNotFoundException(id)
    }

    @Transactional
    fun delete(id: Long) {
        getEntity(id)
            ?.remove()
            ?.also { domainEventPublisher.publish(BrandDeletedEvent(id)) }
            ?: throw DomainNotFoundException(id)
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}
