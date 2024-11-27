package com.musinsa.project.application.ranking

import com.musinsa.project.application.exception.ApplicationException.RankingNotFoundException
import com.musinsa.project.application.ranking.CategoryRankingAccumulator.Companion.makeCategoryRankingKey
import com.musinsa.project.application.ranking.RankingConstants.getCategoryRankingValueBrandId
import com.musinsa.project.application.ranking.TotalRankingAccumulator.Companion.TOTAL_BRAND_RANKING_KEY_NAME
import com.musinsa.project.application.ranking.model.CategoryPriceRankModel
import com.musinsa.project.application.ranking.model.LowestBrandTotalPriceModel
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class RankingDataRetriever(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    fun getLowestPriceRankByCategories(categories: List<Long>): Collection<CategoryPriceRankModel> =
        categories.map { categoryId ->
            getCategoryPriceRankAsc(categoryId)
        }

    fun getCategoryPriceRankAsc(categoryId: Long): CategoryPriceRankModel {
        val key = makeCategoryRankingKey(categoryId)
        val rankings = redisTemplate.opsForZSet().rangeWithScores(key, 0, FETCH_SIZE)
        if (rankings.isNullOrEmpty()) throw RankingNotFoundException()

        val lowestPrice = rankings.mapNotNull { it.score?.toBigDecimal() }.minOrNull() ?: BigDecimal.ZERO
        val target =
            rankings
                .filter { it.score?.toBigDecimal() == lowestPrice }
                .sortedByDescending { it.value?.getCategoryRankingValueBrandId() }
                .firstOrNull()
        return CategoryPriceRankModel(
            categoryId = categoryId,
            brandId = target?.value?.getCategoryRankingValueBrandId(),
            price = target?.score?.toBigDecimal(),
        )
    }

    fun getLowestBrandTotalPriceRank(): LowestBrandTotalPriceModel {
        val ranking =
            redisTemplate
                .opsForZSet()
                .range(TOTAL_BRAND_RANKING_KEY_NAME, 0, FETCH_SIZE)

        if (ranking.isNullOrEmpty()) throw RankingNotFoundException()

        return ranking
            .firstOrNull()
            ?.let { LowestBrandTotalPriceModel(it.toLong()) }
            ?: throw RankingNotFoundException()
    }

    fun getCategoryPriceRankDesc(categoryId: Long): CategoryPriceRankModel {
        val key = makeCategoryRankingKey(categoryId)
        val rankings = redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, FETCH_SIZE)
        if (rankings.isNullOrEmpty()) throw RankingNotFoundException()

        val highestPrice = rankings.mapNotNull { it.score?.toBigDecimal() }.maxOrNull() ?: BigDecimal.ZERO
        val target =
            rankings
                .filter { it.score?.toBigDecimal() == highestPrice }
                .sortedByDescending { it.value?.getCategoryRankingValueBrandId() }
                .firstOrNull()
        return CategoryPriceRankModel(
            categoryId = categoryId,
            brandId = target?.value?.getCategoryRankingValueBrandId(),
            price = target?.score?.toBigDecimal(),
        )
    }

    companion object {
        private const val FETCH_SIZE = 5L
    }
}
