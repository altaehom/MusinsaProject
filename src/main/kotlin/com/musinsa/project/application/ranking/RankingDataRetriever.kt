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

/**
 * 기 저장 된 레디스 데이터를 질의하는 서비스
 */
@Service
class RankingDataRetriever(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    fun getLowestPriceRankByCategories(categories: List<Long>): Collection<CategoryPriceRankModel> =
        categories.map { categoryId ->
            getCategoryPriceRankAsc(categoryId)
        }

    /**
     * 카테고리 별 브랜드 가격 랭킹을 정방향으로 조회 하는 메소드
     * 브랜드 별 랭킹에서 동일한 가격이라면, 비교적 최근에 등록 된 브랜드를 반환
     */
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

    /**
     * 브랜드 가격 총합 랭킹을 조회
     */
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

    /**
     * 카테고리 별 브랜드 가격 랭킹을 역방향으로 조회 하는 메소드
     * 브랜드 별 랭킹에서 동일한 가격이라면, 비교적 최근에 등록 된 브랜드를 반환
     */
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
