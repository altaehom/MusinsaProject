package com.musinsa.project.application.ranking

import com.musinsa.project.application.ranking.event.RankingEvent.ProductRankingChangeEvent
import com.musinsa.project.application.ranking.event.RankingEvent.ProductRankingRemoveAllEvent
import com.musinsa.project.application.ranking.event.RankingEvent.ProductRankingRemoveEvent
import com.musinsa.project.application.ranking.event.RankingEvent.ProductRankingUpsertEvent
import com.musinsa.project.domain.entity.category.Category
import com.musinsa.project.domain.service.category.CategoryDomainService
import com.musinsa.project.domain.service.category.model.CategoryModel
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
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
    private val categoryDomainService: CategoryDomainService = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `handle_ProductRankingRemoveAllEvent_1_카테고리가 조회 되지 않으면 토탈 랭킹만 반영 된다`() {
        val event = ProductRankingRemoveAllEvent(1L)

        every { categoryDomainService.getAll() }.returns(emptyList())

        mut.handle(event)

        verify(exactly = 0) { categoryRankingAccumulator.remove(any(), any()) }
        verify { totalRankingAccumulator.remove("1") }
    }

    @Test
    fun `handle_ProductRankingRemoveAllEvent_2_카테고리와 토탈랭킹이 반영 된다`() {
        val event = ProductRankingRemoveAllEvent(1L)
        val categories =
            listOf(
                spyk<CategoryModel>(
                    CategoryModel(spyk<Category>().apply { this.id = 3 }),
                ),
                spyk<CategoryModel>(
                    CategoryModel(spyk<Category>().apply { this.id = 5 }),
                ),
                spyk<CategoryModel>(
                    CategoryModel(spyk<Category>().apply { this.id = 6 }),
                ),
            )

        every { categoryDomainService.getAll() }.returns(categories)

        mut.handle(event)

        categories.forEach {
            categoryRankingAccumulator.remove(it.id, "1")
        }
        verify { totalRankingAccumulator.remove("1") }
    }

    @Test
    fun `handle_ProductRankingRemoveEvent_카테고리 랭킹과 토탈 랭킹이 반영 된다`() {
        val event =
            ProductRankingRemoveEvent(
                brandId = 1L,
                categoryId = 2L,
            )

        mut.handle(event)

        verifyOrder {
            categoryRankingAccumulator.remove(
                categoryId = event.categoryId,
                value = event.brandId.toString(),
            )
            totalRankingAccumulator.remove(event.brandId.toString())
        }
    }

    @Test
    fun `handle_ProductRankingUpsertEvent_카테고리 랭킹과 토탈 랭킹이 반영 된다`() {
        val event =
            ProductRankingUpsertEvent(
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
                value = event.brandId.toString(),
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
                brandId = 1L,
                categoryId = 2L,
                price = BigDecimal.TEN,
            )

        mut.handle(event)

        verifyOrder {
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
}
