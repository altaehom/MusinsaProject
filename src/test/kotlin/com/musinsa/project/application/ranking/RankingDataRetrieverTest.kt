package com.musinsa.project.application.ranking

import com.musinsa.project.application.exception.ApplicationException.RankingNotFoundException
import com.musinsa.project.application.ranking.model.CategoryPriceRankModel
import com.musinsa.project.application.ranking.model.LowestBrandTotalPriceModel
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations.TypedTuple
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@ExtendWith(MockKExtension::class)
class RankingDataRetrieverTest {
    @InjectMockKs
    private lateinit var mut: RankingDataRetriever
    private val redisTemplate: RedisTemplate<String, String> = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `getCategoryPriceRankAsc_1_랭킹을 찾지 못하면 에러를 반환한다`() {
        val categoryId = 1L
        val key = "CATEGORY::$categoryId"

        every { redisTemplate.opsForZSet().rangeWithScores(key, 0, 5) }.returns(null)

        assertFailsWith<RankingNotFoundException> {
            mut.getCategoryPriceRankAsc(categoryId)
        }
    }

    @Test
    fun `getCategoryPriceRankAsc_1_반환 된 값중 제일 작은 값이 반환된다`() {
        val categoryId = 1L
        val key = "CATEGORY::$categoryId"

        every { redisTemplate.opsForZSet().rangeWithScores(key, 0, 5) }.returns(
            setOf(
                TypedTuple.of(
                    "1",
                    1.0,
                ),
                TypedTuple.of(
                    "2",
                    2.0,
                ),
                TypedTuple.of(
                    "3",
                    3.0,
                ),
            ),
        )

        val result = mut.getCategoryPriceRankAsc(categoryId)

        assertEquals(
            result,
            CategoryPriceRankModel(
                categoryId = categoryId,
                brandId = 1,
                price = 1.0.toBigDecimal(),
            ),
        )
    }

    @Test
    fun `getCategoryPriceRankAsc_2_동일한 Score면 비교적 최근에 등록 된 값이 반환된다`() {
        val categoryId = 1L
        val key = "CATEGORY::$categoryId"

        every { redisTemplate.opsForZSet().rangeWithScores(key, 0, 5) }.returns(
            setOf(
                TypedTuple.of(
                    "1",
                    1.0,
                ),
                TypedTuple.of(
                    "2",
                    1.0,
                ),
                TypedTuple.of(
                    "3",
                    3.0,
                ),
            ),
        )

        val result = mut.getCategoryPriceRankAsc(categoryId)

        assertEquals(
            result,
            CategoryPriceRankModel(
                categoryId = categoryId,
                brandId = 2,
                price = 1.0.toBigDecimal(),
            ),
        )
    }

    @Test
    fun `getLowestBrandTotalPriceRank_1_가장 작은 값이 반환된다`() {
        val key = "TOTAL::Brand"

        every { redisTemplate.opsForZSet().range(key, 0, 5) }.returns(
            setOf("1", "2"),
        )

        val result = mut.getLowestBrandTotalPriceRank()
        assertEquals(result, LowestBrandTotalPriceModel(1))
    }

    @Test
    fun `getLowestBrandTotalPriceRank_2_랭킹을 찾지 못하면 에러를 반환한다`() {
        val key = "TOTAL::Brand"

        every { redisTemplate.opsForZSet().range(key, 0, 5) }.returns(null)

        assertFailsWith<RankingNotFoundException> {
            mut.getLowestBrandTotalPriceRank()
        }
    }

    @Test
    fun `getCategoryPriceRankDesc_1_반환 된 값중 제일 큰 값이 반환된다`() {
        val categoryId = 1L
        val key = "CATEGORY::$categoryId"

        every { redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, 5) }.returns(
            setOf(
                TypedTuple.of(
                    "1",
                    1.0,
                ),
                TypedTuple.of(
                    "2",
                    2.0,
                ),
                TypedTuple.of(
                    "3",
                    3.0,
                ),
            ),
        )

        val result = mut.getCategoryPriceRankDesc(categoryId)

        assertEquals(
            result,
            CategoryPriceRankModel(
                categoryId = categoryId,
                brandId = 3,
                price = 3.0.toBigDecimal(),
            ),
        )
    }

    @Test
    fun `getCategoryPriceRankDesc_2_동일한 Score면 비교적 최근에 등록 된 값이 반환된다`() {
        val categoryId = 1L
        val key = "CATEGORY::$categoryId"

        every { redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, 5) }.returns(
            setOf(
                TypedTuple.of(
                    "1",
                    4.0,
                ),
                TypedTuple.of(
                    "2",
                    4.0,
                ),
                TypedTuple.of(
                    "3",
                    3.0,
                ),
            ),
        )

        val result = mut.getCategoryPriceRankDesc(categoryId)

        assertEquals(
            result,
            CategoryPriceRankModel(
                categoryId = categoryId,
                brandId = 2,
                price = 4.0.toBigDecimal(),
            ),
        )
    }

    @Test
    fun `getCategoryPriceRankDesc_3_랭킹을 찾지 못하면 에러를 반환한다`() {
        val categoryId = 1L
        val key = "CATEGORY::$categoryId"

        every { redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, 5) }.returns(null)

        assertFailsWith<RankingNotFoundException> {
            mut.getCategoryPriceRankDesc(categoryId)
        }
    }
}
