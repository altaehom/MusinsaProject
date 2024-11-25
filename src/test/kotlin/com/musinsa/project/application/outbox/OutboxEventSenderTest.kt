package com.musinsa.project.application.outbox

import com.musinsa.project.application.ranking.event.RankingEvent
import com.musinsa.project.application.ranking.event.RankingEventConverter
import com.musinsa.project.application.ranking.event.RankingEventType.PRODUCT_REMOVED_EVENT
import com.musinsa.project.domain.entity.outbox.Outbox
import com.musinsa.project.domain.service.outbox.OutboxDomainService
import com.musinsa.project.infra.event.DomainEventPublisher
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID
import kotlin.test.assertFailsWith

@ExtendWith(MockKExtension::class)
class OutboxEventSenderTest {
    @InjectMockKs
    private lateinit var mut: OutboxEventSender
    private val outboxDomainService: OutboxDomainService = mockk(relaxed = true)
    private val domainEventPublisher: DomainEventPublisher = mockk(relaxed = true)
    private val rankingEventConverter: RankingEventConverter = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `send_1_데이터를 조회 할 수 없으면 return 된다`() {
        val id = 1L

        every { outboxDomainService.getOne(id) }.returns(null)

        mut.send(id)

        verify(exactly = 0) { outboxDomainService.markPublish(any()) }
        verify(exactly = 0) { domainEventPublisher.publish(any()) }
    }

    @Test
    fun `send_2_payload가 공백이면 에러를 반환한다`() {
        val id = 1L

        every { outboxDomainService.getOne(id) }.returns(
            Outbox(
                eventId = UUID.randomUUID().toString(),
                eventType = "Test",
                aggregateType = "aaaa",
                header = "aa",
                payload = "",
            ),
        )

        assertFailsWith<IllegalStateException> {
            mut.send(id)
        }

        verify(exactly = 0) { outboxDomainService.markPublish(any()) }
        verify(exactly = 0) { domainEventPublisher.publish(any()) }
    }

    @Test
    fun `send_3_이벤트 타입이 정의되어 있는 타입이 아니라면 return 된다`() {
        val id = 1L
        val outbox =
            Outbox(
                eventId = UUID.randomUUID().toString(),
                eventType = "Test",
                aggregateType = "aaaa",
                header = "aa",
                payload = "aaaa",
            )

        every { outboxDomainService.getOne(id) }.returns(outbox)

        mut.send(id)

        verify { outboxDomainService.markPublish(outbox) }
        verify(exactly = 0) { domainEventPublisher.publish(any()) }
    }

    @Test
    fun `send_5_이벤트가 사출 되면 publish 처리 된다`() {
        val id = 1L
        val outbox =
            Outbox(
                eventId = UUID.randomUUID().toString(),
                eventType = PRODUCT_REMOVED_EVENT.eventName,
                aggregateType = "aaaa",
                header = "aa",
                payload = "aaaa",
            )

        every { outboxDomainService.getOne(id) }.returns(outbox)

        mut.send(id)

        verify { outboxDomainService.markPublish(outbox) }
        verify { domainEventPublisher.publish(ofType(RankingEvent::class)) }
    }
}
