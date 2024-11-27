package com.musinsa.project.application.ranking

import com.musinsa.project.application.ranking.RankingConstants.getCategoryRankingValueName
import com.musinsa.project.application.ranking.event.RankingEvent.ProductRankingChangeEvent
import com.musinsa.project.application.ranking.event.RankingEvent.ProductRankingRemoveEvent
import com.musinsa.project.application.ranking.event.RankingEvent.ProductRankingUpsertEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class RankingEventListener(
    private val categoryRankingAccumulator: CategoryRankingAccumulator,
    private val totalRankingAccumulator: TotalRankingAccumulator,
) {
    @EventListener
    fun handle(event: ProductRankingRemoveEvent) {
        categoryRankingAccumulator.remove(
            categoryId = event.categoryId,
            value =
                getCategoryRankingValueName(
                    productId = event.id,
                    brandId = event.brandId,
                ),
        )

        totalRankingAccumulator.remove("${event.brandId}")
    }

    @EventListener
    fun handle(event: ProductRankingUpsertEvent) {
        categoryRankingAccumulator.accumulate(
            score = event.price,
            categoryId = event.categoryId,
            value =
                getCategoryRankingValueName(
                    productId = event.id,
                    brandId = event.brandId,
                ),
        )

        totalRankingAccumulator.accumulate(
            score = event.priceDiff(),
            value = "${event.brandId}",
        )
    }

    @EventListener
    fun handle(event: ProductRankingChangeEvent) {
        categoryRankingAccumulator.remove(
            categoryId = event.categoryId,
            value =
                getCategoryRankingValueName(
                    productId = event.id,
                    brandId = event.brandId,
                ),
        )

        totalRankingAccumulator.accumulate(
            score = event.priceDiff(),
            value = event.brandId.toString(),
        )
    }
}
