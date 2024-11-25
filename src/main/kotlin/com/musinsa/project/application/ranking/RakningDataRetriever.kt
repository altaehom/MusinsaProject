package com.musinsa.project.application.ranking

import com.musinsa.project.application.ranking.CategoryRankingAccumulator.Companion.makeCategoryRankingKey
import com.musinsa.project.application.ranking.model.LowestPriceRankModel
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class RakningDataRetriever(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    fun getLowestPriceRankByCategory(categories: List<Long>): Collection<LowestPriceRankModel> {
        val zSetOps = redisTemplate.opsForZSet()
        return categories.map { categoryId ->
            val key = makeCategoryRankingKey(categoryId)
            val data = zSetOps.rangeWithScores(key, 0, 0)?.firstOrNull()
            LowestPriceRankModel(
                categoryId = categoryId,
                brandId = data?.value?.toLong(),
                price = data?.score?.toBigDecimal(),
            )
        }
    }
}
