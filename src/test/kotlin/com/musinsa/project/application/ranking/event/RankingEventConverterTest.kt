package com.musinsa.project.application.ranking.event

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.musinsa.project.application.ranking.event.RankingEvent.ProductRankingChangeEvent
import com.musinsa.project.application.ranking.event.RankingEvent.ProductRankingRemoveAllEvent
import com.musinsa.project.application.ranking.event.RankingEvent.ProductRankingRemoveEvent
import com.musinsa.project.application.ranking.event.RankingEvent.ProductRankingUpsertEvent
import com.musinsa.project.application.ranking.event.RankingEventType.BRAND_DELETED_EVENT
import com.musinsa.project.application.ranking.event.RankingEventType.PRODUCT_CREATED_EVENT
import com.musinsa.project.application.ranking.event.RankingEventType.PRODUCT_DELETED_EVENT
import com.musinsa.project.application.ranking.event.RankingEventType.PRODUCT_REMOVED_EVENT
import com.musinsa.project.application.ranking.event.RankingEventType.PRODUCT_UPDATED_EVENT
import com.musinsa.project.application.ranking.event.RankingEventType.UNKNOWN
import com.musinsa.project.domain.service.brand.event.BrandDomainEvent.BrandDeletedEvent
import com.musinsa.project.domain.service.product.event.ProductDomainEvent.ProductCreatedEvent
import com.musinsa.project.domain.service.product.event.ProductDomainEvent.ProductDeletedEvent
import com.musinsa.project.domain.service.product.event.ProductDomainEvent.ProductRemovedEvent
import com.musinsa.project.domain.service.product.event.ProductDomainEvent.ProductUpdatedEvent
import io.mockk.clearAllMocks
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@ExtendWith(MockKExtension::class)
class RankingEventConverterTest {
    @InjectMockKs
    private lateinit var mut: RankingEventConverter
    private val objectMapper = jacksonObjectMapper().registerKotlinModule().registerModules(JavaTimeModule())
    private val now = Instant.now()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `convertEvent_1_브랜드가 삭제 되었으면 랭킹 전체 삭제 이벤트로 변환된다`() {
        val type = BRAND_DELETED_EVENT
        val payload =
            objectMapper.writeValueAsString(
                BrandDeletedEvent(1L),
            )

        val result =
            mut.convertEvent(
                type = type,
                payload = payload,
            )

        assertEquals(result, ProductRankingRemoveAllEvent(1L))
    }

    @Test
    fun `convertEvent_2_브랜드가 삭제 되어, 제품이 제거 되었으면 랭킹 삭제 이벤트로 변환된다`() {
        val type = PRODUCT_DELETED_EVENT
        val payload =
            objectMapper.writeValueAsString(
                ProductDeletedEvent(1L, 2L, 3L, BigDecimal.TEN),
            )

        val result =
            mut.convertEvent(
                type = type,
                payload = payload,
            )

        assertEquals(result, ProductRankingRemoveEvent(2L, 3L))
    }

    @Test
    fun `convertEvent_3_제품이 제거 되었으면 랭킹 변경 이벤트로 변환된다`() {
        val type = PRODUCT_REMOVED_EVENT
        val payload =
            objectMapper.writeValueAsString(
                ProductRemovedEvent(1L, 2L, 3L, BigDecimal.TEN),
            )

        val result =
            mut.convertEvent(
                type = type,
                payload = payload,
            )

        assertEquals(
            result,
            ProductRankingChangeEvent(
                brandId = 2L,
                categoryId = 3L,
                price = BigDecimal.TEN,
            ),
        )
    }

    @Test
    fun `convertEvent_4_제품이 수정 되었으면 랭킹 수정 이벤트로 변환된다`() {
        val type = PRODUCT_UPDATED_EVENT
        val payload =
            objectMapper.writeValueAsString(
                ProductUpdatedEvent(
                    id = 1L,
                    categoryId = 2L,
                    brandId = 3L,
                    price = BigDecimal.TEN,
                    beforePrice = BigDecimal.ONE,
                    createdAt = now,
                    updatedAt = now,
                ),
            )

        val result =
            mut.convertEvent(
                type = type,
                payload = payload,
            )

        assertEquals(
            result,
            ProductRankingUpsertEvent(
                brandId = 3L,
                categoryId = 2L,
                price = BigDecimal.TEN,
                beforePrice = BigDecimal.ONE,
            ),
        )
    }

    @Test
    fun `convertEvent_5_제품이 추가 되었으면 랭킹 수정 이벤트로 변환된다`() {
        val type = PRODUCT_CREATED_EVENT
        val payload =
            objectMapper.writeValueAsString(
                ProductCreatedEvent(
                    id = 1L,
                    categoryId = 2L,
                    brandId = 3L,
                    price = BigDecimal.TEN,
                    createdAt = now,
                    updatedAt = null,
                ),
            )

        val result =
            mut.convertEvent(
                type = type,
                payload = payload,
            )

        assertEquals(
            result,
            ProductRankingUpsertEvent(
                brandId = 3L,
                categoryId = 2L,
                price = BigDecimal.TEN,
                beforePrice = null,
            ),
        )
    }

    @Test
    fun `convertEvent_6_모르는 이벤트면 에러를 반환한다`() {
        val type = UNKNOWN

        assertFailsWith<IllegalArgumentException> {
            mut.convertEvent(
                type = type,
                payload = "aaa",
            )
        }
    }
}
