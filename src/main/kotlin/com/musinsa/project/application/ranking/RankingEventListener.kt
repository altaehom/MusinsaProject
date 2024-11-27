package com.musinsa.project.application.ranking

import com.musinsa.project.application.ranking.RankingConstants.getCategoryRankingValueName
import com.musinsa.project.application.ranking.event.RankingEvent.ProductRankingChangeEvent
import com.musinsa.project.application.ranking.event.RankingEvent.ProductRankingRemoveEvent
import com.musinsa.project.application.ranking.event.RankingEvent.ProductRankingUpsertEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

/**
 * 전달 받은 랭킹 이벤트를 가지고 랭킹에 적재 처리를 위임하는 이벤트 리스너
 */
@Component
class RankingEventListener(
    private val categoryRankingAccumulator: CategoryRankingAccumulator,
    private val totalRankingAccumulator: TotalRankingAccumulator,
) {
    /**
     * 브랜드가 삭제 되었을 경우, 브랜드 하위에 존재하는 제품을 카테고리 랭킹에 따라 삭제 처리, 총합 랭킹에서도 제거
     */
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

    /**
     * 제품이 추가/수정 되었을 때 가격 편차를 기준으로 zset의 score를 변경
     */
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

    /**
     * 제품 삭제 되었을 때, 카테고리 랭킹에 존재하는 브랜드 상품을 삭제, 총합 랭킹에서는 삭제 된 상품의 가격 만큼 Score를 차감
     */
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
