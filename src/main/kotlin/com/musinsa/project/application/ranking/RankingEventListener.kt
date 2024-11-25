package com.musinsa.project.application.ranking

import com.musinsa.project.application.ranking.event.RankingEvent.ProductRankingChangeEvent
import com.musinsa.project.application.ranking.event.RankingEvent.ProductRankingRemoveAllEvent
import com.musinsa.project.application.ranking.event.RankingEvent.ProductRankingRemoveEvent
import com.musinsa.project.application.ranking.event.RankingEvent.ProductRankingUpsertEvent
import com.musinsa.project.domain.service.category.CategoryDomainService
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class RankingEventListener(
    private val categoryRankingAccumulator: CategoryRankingAccumulator,
    private val totalRankingAccumulator: TotalRankingAccumulator,
    private val categoryDomainService: CategoryDomainService,
) {
    @EventListener
    fun handle(event: ProductRankingRemoveAllEvent) {
        val categories = categoryDomainService.getAll()

        categories.forEach {
            categoryRankingAccumulator.remove(
                categoryId = it.id,
                value = event.brandId.toString(),
            )
        }

        totalRankingAccumulator.remove(event.brandId.toString())
    }

    @EventListener
    fun handle(event: ProductRankingRemoveEvent) {
        categoryRankingAccumulator.remove(
            categoryId = event.categoryId,
            value = event.brandId.toString(),
        )

        totalRankingAccumulator.remove(event.brandId.toString())
    }

    @EventListener
    fun handle(event: ProductRankingUpsertEvent) {
        categoryRankingAccumulator.accumulate(
            score = event.price,
            categoryId = event.categoryId,
            value = event.brandId.toString(),
        )

        totalRankingAccumulator.accumulate(
            score = event.priceDiff(),
            value = event.brandId.toString(),
        )
    }

    @EventListener
    fun handle(event: ProductRankingChangeEvent) {
        categoryRankingAccumulator.remove(
            categoryId = event.categoryId,
            value = event.brandId.toString(),
        )

        totalRankingAccumulator.accumulate(
            score = event.priceDiff(),
            value = event.brandId.toString(),
        )
    }
}
