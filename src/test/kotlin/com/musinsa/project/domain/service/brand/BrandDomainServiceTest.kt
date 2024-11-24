package com.musinsa.project.domain.service.brand

import com.musinsa.project.domain.entity.brand.Brand
import com.musinsa.project.domain.entity.brand.BrandRepository
import com.musinsa.project.domain.exception.DomainException.DomainNotFoundException
import com.musinsa.project.domain.service.brand.event.BrandDomainEvent.BrandCreatedEvent
import com.musinsa.project.domain.service.brand.event.BrandDomainEvent.BrandDeletedEvent
import com.musinsa.project.domain.service.brand.event.BrandDomainEvent.BrandUpdatedEvent
import com.musinsa.project.domain.service.brand.model.BrandModel
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
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@ExtendWith(MockKExtension::class)
class BrandDomainServiceTest {
    @InjectMockKs
    private lateinit var mut: BrandDomainService
    private val brandRepository: BrandRepository = mockk(relaxed = true)
    private val domainEventPublisher: DomainEventPublisher = mockk(relaxed = true)
    private val now = Instant.now()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun `save_1_데이터 저장 시에 save는 반드시 호출 된다`() {
        val brandName = "Test"
        val slot = slot<Brand>()

        every { brandRepository.save(capture(slot)) }.answers { slot.captured }

        mut.save(brandName)

        verifySequence {
            brandRepository.save(slot.captured)
            domainEventPublisher.publish(
                withArg {
                    it is BrandCreatedEvent && it.brandName == brandName
                },
            )
        }
        assertEquals(slot.captured.brandName, brandName)
    }

    @Test
    fun `get_1_데이터가 조회 되지 않으면 null을 반환한다`() {
        val id = 1L
        every { brandRepository.findByIdOrNull(any()) }.returns(null)

        val result = mut.get(id)
        assertNull(result)
    }

    @Test
    fun `get_2_데이터가 존재하면 Domain 모델이 반환된다`() {
        val id = 1L
        val brandName = "Test"
        val mockkBrand =
            spyk<Brand>().apply {
                this.id = id
                this.brandName = brandName
            }
        mockkStatic(Instant::class)
        every { Instant.now() }.returns(now)
        every { brandRepository.findByIdOrNull(id) }.returns(mockkBrand)

        val result = mut.get(id)
        assertNotNull(result)
        assertEquals(result, BrandModel(mockkBrand))
    }

    @Test
    fun `update_1_엔티티가 조회 되지 않으면 에러가 반환된다`() {
        val id = 1L

        every { brandRepository.findByIdOrNull(id) }.returns(null)

        assertFailsWith<DomainNotFoundException> {
            mut.update(id, "")
        }
    }

    @Test
    fun `update_2_값이 변경 될 경우, 엔티티에 변경이 일어난다`() {
        val id = 1L
        val brandName = "TestBrand"
        val changeBrandName = "TestBrand2"
        val mockkBrand =
            spyk<Brand>().apply {
                this.id = id
                this.brandName = brandName
            }

        every { brandRepository.findByIdOrNull(id) }.returns(mockkBrand)

        mut.update(id, changeBrandName)

        verifyOrder {
            mockkBrand.modify(changeBrandName)
            domainEventPublisher.publish(
                withArg {
                    it is BrandUpdatedEvent && it.brandName == brandName && it.id == id
                },
            )
        }
        assertEquals(mockkBrand.brandName, changeBrandName)
    }

    @Test
    fun `delete_1_엔티티가 조회 되지 않으면 에러가 반환된다`() {
        val id = 1L

        every { brandRepository.findByIdOrNull(id) }.returns(null)

        assertFailsWith<DomainNotFoundException> {
            mut.delete(id)
        }
    }

    @Test
    fun `delete_2_값이 변경 될 경우, 엔티티에 변경이 일어난다`() {
        val id = 1L
        val brandName = "TestBrand"
        val mockkBrand =
            spyk<Brand>().apply {
                this.id = id
                this.brandName = brandName
            }

        every { brandRepository.findByIdOrNull(id) }.returns(mockkBrand)

        mut.delete(id)

        verifyOrder {
            mockkBrand.remove()
            domainEventPublisher.publish(
                withArg {
                    it is BrandDeletedEvent && it.id == id
                },
            )
        }
        assertTrue(mockkBrand.deleted)
    }
}
