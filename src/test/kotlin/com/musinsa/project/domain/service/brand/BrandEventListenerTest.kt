package com.musinsa.project.domain.service.brand

import com.musinsa.project.domain.service.brand.event.BrandDomainEvent.BrandCreatedEvent
import com.musinsa.project.domain.service.brand.event.BrandDomainEvent.BrandDeletedEvent
import com.musinsa.project.domain.service.product.ProductDomainService
import io.mockk.called
import io.mockk.clearAllMocks
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Instant

@ExtendWith(MockKExtension::class)
class BrandEventListenerTest {
    @InjectMockKs
    private lateinit var mut: BrandEventListener
    private val productDomainService: ProductDomainService = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `handler_1_Delete 이벤트가 아니면 return 된다`() {
        val event =
            BrandCreatedEvent(
                id = 1L,
                brandName = "aa",
                createdAt = Instant.now(),
                updatedAt = null,
            )

        mut.handler(event)

        verify { productDomainService wasNot called }
    }

    @Test
    fun `handler_2_Delete 이벤트면 메소드가 호출 된다`() {
        val event = BrandDeletedEvent(1L)

        mut.handler(event)

        verify { productDomainService.deleteByBrandId(event.id) }
    }
}
