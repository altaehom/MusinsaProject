package com.musinsa.project.domain.service.brand

import com.musinsa.project.domain.service.brand.event.BrandDomainEvent
import com.musinsa.project.domain.service.brand.event.BrandDomainEvent.BrandDeletedEvent
import com.musinsa.project.domain.service.product.ProductDomainService
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class BrandEventListener(
    private val productDomainService: ProductDomainService,
) {
    @Transactional
    @EventListener
    fun handler(event: BrandDomainEvent) {
        if ((event is BrandDeletedEvent).not()) return

        productDomainService.deleteByBrandId(event.id)
    }
}
