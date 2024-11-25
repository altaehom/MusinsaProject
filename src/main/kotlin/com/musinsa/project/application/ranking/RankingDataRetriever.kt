package com.musinsa.project.application.ranking

import com.musinsa.project.application.ranking.CategoryRankingAccumulator.Companion.makeCategoryRankingKey
import com.musinsa.project.application.ranking.TotalRankingAccumulator.Companion.TOTAL_BRAND_RANKING_KEY_NAME
import com.musinsa.project.application.ranking.model.LowestBrandTotalPriceModel
import com.musinsa.project.application.ranking.model.LowestCategoryPriceRankModel
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class RankingDataRetriever(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    fun getLowestPriceRankByCategory(categories: List<Long>): Collection<LowestCategoryPriceRankModel> {
        val zSetOps = redisTemplate.opsForZSet()
        return categories.map { categoryId ->
            val key = makeCategoryRankingKey(categoryId)
            val data = zSetOps.rangeWithScores(key, 0, 0)?.firstOrNull()
            LowestCategoryPriceRankModel(
                categoryId = categoryId,
                brandId = data?.value?.toLong(),
                price = data?.score?.toBigDecimal(),
            )
        }
    }

    fun getLowestBrandTotalPriceRank(): LowestBrandTotalPriceModel? =
        redisTemplate
            .opsForZSet()
            .range(TOTAL_BRAND_RANKING_KEY_NAME, 0, 0)
            ?.firstOrNull()
            ?.let { LowestBrandTotalPriceModel(it.toLong()) }
}
