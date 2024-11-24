package com.musinsa.project.domain.service.outbox

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.musinsa.project.domain.entity.outbox.Outbox
import com.musinsa.project.domain.entity.outbox.OutboxRepository
import com.musinsa.project.infra.event.CloudEvent
import com.musinsa.project.infra.event.Headers
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(MockKExtension::class)
class OutboxDomainServiceTest {
    @InjectMockKs
    private lateinit var mut: OutboxDomainService
    private val outboxRepository: OutboxRepository = mockk(relaxed = true)
    private val objectMapper: ObjectMapper = jacksonObjectMapper().registerKotlinModule()

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `getOne_1_값이 조회되지 않으면 null을 반환한다`() {
        every { outboxRepository.findByIdOrNull(any()) }.returns(null)
        assertNull(mut.getOne(1L))
    }

    @Test
    fun `getNotPublished_1_리턴 되는 값은 배열이 반환된다`() {
        every { outboxRepository.findByPublishedIsFalseOrderByIdDescLimit100() }.returns(emptyList())

        val result = mut.getNotPublished()
        assertEquals(emptyList(), result)
    }

    @Test
    fun `save_1_저장 시에는 save 메소드가 호출 된다`() {
        val event =
            CloudEvent(
                event = "aa",
                eventType = "aa",
                aggregateType = "aa",
                header = Headers("aa"),
            )

        val slot = slot<Outbox>()

        every { outboxRepository.save(capture(slot)) }.answers { slot.captured }

        mut.save(event)

        verify {
            outboxRepository.save(slot.captured)
        }
    }

    @Test
    fun `markPublish_1_published 함수는 반드시 호출된다`() {
        val mockOutbox = spyk<Outbox>()

        mut.markPublish(mockOutbox)

        verify { mockOutbox.published() }

        assertTrue(mockOutbox.published)
        assertNotNull(mockOutbox.processedAt)
    }
}
