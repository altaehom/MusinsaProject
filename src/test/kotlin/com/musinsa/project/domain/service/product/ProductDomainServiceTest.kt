package com.musinsa.project.domain.service.product

import com.musinsa.project.domain.entity.product.Product
import com.musinsa.project.domain.entity.product.ProductRepository
import com.musinsa.project.domain.exception.DomainException.DomainNotFoundException
import com.musinsa.project.domain.service.product.event.ProductDomainEvent.ProductCreatedEvent
import com.musinsa.project.domain.service.product.event.ProductDomainEvent.ProductRemovedEvent
import com.musinsa.project.domain.service.product.event.ProductDomainEvent.ProductUpdatedEvent
import com.musinsa.project.domain.service.product.model.ProductModel
import com.musinsa.project.infra.event.DomainEventPublisher
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verifyOrder
import io.mockk.verifySequence
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull
import java.math.BigDecimal
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@ExtendWith(MockKExtension::class)
class ProductDomainServiceTest {
    @InjectMockKs
    private lateinit var mut: ProductDomainService
    private val productRepository: ProductRepository = mockk(relaxed = true)
    private val domainEventPublisher: DomainEventPublisher = mockk(relaxed = true)
    private val now = Instant.now()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        mockkStatic(Instant::class)
        every { Instant.now() }.returns(now)
    }

    @Test
    fun `save_1_데이터 저장 시에 save는 반드시 호출 된다`() {
        val brandId = 1L
        val categoryId = 1L
        val price = BigDecimal.valueOf(102030130)
        val slot = slot<Product>()

        every { productRepository.save(capture(slot)) }.answers { slot.captured }

        mut.save(
            brandId = brandId,
            categoryId = categoryId,
            price = price,
        )

        verifySequence {
            productRepository.save(slot.captured)
            domainEventPublisher.publish(
                withArg {
                    it is ProductCreatedEvent && it.price == price && it.brandId == brandId && it.categoryId == categoryId
                },
            )
        }
        assertEquals(slot.captured.price, price)
        assertEquals(slot.captured.brandId, brandId)
        assertEquals(slot.captured.categoryId, categoryId)
    }

    @Test
    fun `get_1_데이터가 조회 되지 않으면 null을 반환한다`() {
        val id = 1L
        every { productRepository.findByIdOrNull(any()) }.returns(null)

        val result = mut.get(id)
        assertNull(result)
    }

    @Test
    fun `get_2_데이터가 존재하면 Domain 모델이 반환된다`() {
        val id = 1L
        val brandId = 1L
        val categoryId = 1L
        val price = BigDecimal.valueOf(102030130)
        val mockkProduct =
            spyk<Product>(
                Product(
                    categoryId = categoryId,
                    brandId = brandId,
                )
            ).apply {
                this.id = id
                this.price = price
            }
        every { productRepository.findByIdOrNull(id) }.returns(mockkProduct)

        val result = mut.get(id)
        assertNotNull(result)
        assertEquals(result, ProductModel(mockkProduct))
    }

    @Test
    fun `update_1_엔티티가 조회 되지 않으면 에러가 반환된다`() {
        val id = 1L

        every { productRepository.findByIdOrNull(id) }.returns(null)

        assertFailsWith<DomainNotFoundException> {
            mut.update(
                id = id,
                price = BigDecimal.TEN,
            )
        }
    }

    @Test
    fun `update_2_값이 변경 될 경우, 엔티티에 변경이 일어난다`() {
        val id = 1L
        val brandId = 1L
        val categoryId = 1L
        val price = BigDecimal.valueOf(102030130)
        val changePrice = BigDecimal.valueOf(19)
        val mockkProduct =
            spyk<Product>(
                Product(
                    categoryId = categoryId,
                    brandId = brandId,
                ).apply {
                    this.id = id
                    this.price = price
                },
            )
        every { productRepository.findByIdOrNull(id) }.returns(mockkProduct)

        mut.update(
            id = id,
            price = changePrice,
        )

        verifyOrder {
            mockkProduct.modify(changePrice)
            domainEventPublisher.publish(
                withArg {
                    it is ProductUpdatedEvent &&
                        it.beforePrice == price &&
                        it.price == changePrice &&
                        it.brandId == brandId &&
                        it.categoryId == categoryId
                },
            )
        }
        assertEquals(mockkProduct.price, changePrice)
        assertEquals(mockkProduct.updatedAt, now)
    }

    @Test
    fun `delete_1_엔티티가 조회 되지 않으면 에러가 반환된다`() {
        val id = 1L

        every { productRepository.findByIdOrNull(id) }.returns(null)

        assertFailsWith<DomainNotFoundException> {
            mut.delete(id)
        }
    }

    @Test
    fun `delete_2_값이 변경 될 경우, 엔티티에 변경이 일어난다`() {
        val id = 1L
        val brandId = 1L
        val categoryId = 1L
        val price = BigDecimal.valueOf(102030130)
        val mockkProduct =
            spyk<Product>(
                Product(
                    categoryId = categoryId,
                    brandId = brandId,
                ).apply {
                    this.id = id
                    this.price = price
                },
            )

        every { productRepository.findByIdOrNull(id) }.returns(mockkProduct)

        mut.delete(id)

        verifyOrder {
            mockkProduct.remove()
            domainEventPublisher.publish(
                withArg {
                    it is ProductRemovedEvent && it.brandId == brandId && it.categoryId == categoryId && it.price == price
                },
            )
        }
        assertTrue(mockkProduct.deleted)
    }
}
