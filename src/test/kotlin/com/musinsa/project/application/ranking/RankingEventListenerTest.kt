package com.musinsa.project.application.ranking

import com.musinsa.project.application.ranking.event.RankingEvent.ProductRankingChangeEvent
import com.musinsa.project.application.ranking.event.RankingEvent.ProductRankingRemoveEvent
import com.musinsa.project.application.ranking.event.RankingEvent.ProductRankingUpsertEvent
import io.mockk.clearAllMocks
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verifyOrder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal

@ExtendWith(MockKExtension::class)
class RankingEventListenerTest {
    @InjectMockKs
    private lateinit var mut: RankingEventListener
    private val categoryRankingAccumulator: CategoryRankingAccumulator = mockk(relaxed = true)
    private val totalRankingAccumulator: TotalRankingAccumulator = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `handle_ProductRankingRemoveEvent_카테고리 랭킹과 토탈 랭킹이 반영 된다`() {
        val event =
            ProductRankingRemoveEvent(
                id = 3L,
                brandId = 1L,
                categoryId = 2L,
            )

        mut.handle(event)

        verifyOrder {
            categoryRankingAccumulator.remove(
                categoryId = event.categoryId,
                value = "${event.id}-${event.brandId}",
            )
            totalRankingAccumulator.remove(event.brandId.toString())
        }
    }

    @Test
    fun `handle_ProductRankingUpsertEvent_카테고리 랭킹과 토탈 랭킹이 반영 된다`() {
        val event =
            ProductRankingUpsertEvent(
                id = 3L,
                brandId = 1L,
                categoryId = 2L,
                beforePrice = BigDecimal.ZERO,
                price = BigDecimal.TEN,
            )

        mut.handle(event)

        verifyOrder {
            categoryRankingAccumulator.accumulate(
                score = event.price,
                categoryId = event.categoryId,
                value = "${event.id}-${event.brandId}",
            )
            totalRankingAccumulator.accumulate(
                score = event.priceDiff(),
                value = event.brandId.toString(),
            )
        }
    }

    @Test
    fun `handle_ProductRankingChangeEvent_카테고리은 삭제 되고 토탈 랭킹이 반영 된다`() {
        val event =
            ProductRankingChangeEvent(
                id = 3L,
                brandId = 1L,
                categoryId = 2L,
                price = BigDecimal.TEN,
            )

        mut.handle(event)

        verifyOrder {
            categoryRankingAccumulator.remove(
                categoryId = event.categoryId,
                value = "${event.id}-${event.brandId}",
            )
            totalRankingAccumulator.accumulate(
                score = event.priceDiff(),
                value = event.brandId.toString(),
            )
        }
    }
}
