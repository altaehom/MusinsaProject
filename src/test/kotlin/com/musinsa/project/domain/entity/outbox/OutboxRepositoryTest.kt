package com.musinsa.project.domain.entity.outbox

import io.mockk.clearAllMocks
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals

@SpringBootTest(
    properties = ["classpath:application-test.yml"],
)
@Transactional
class OutboxRepositoryTest {
    @Autowired
    private lateinit var outboxRepository: OutboxRepository

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `CRD Test`() {
        val outbox =
            Outbox(
                eventType = "Test",
                aggregateType = "Test",
                header = "Test",
                payload = "Test",
                eventId = "Test",
            ).let { outboxRepository.save(it) }

        val outboxs = outboxRepository.findByPublishedIsFalseOrderByIdDescLimit100()
        assertEquals(listOf(outbox), outboxs)

        outboxRepository.delete(outbox)
        assertEquals(outboxRepository.findByPublishedIsFalseOrderByIdDescLimit100(), emptyList())
    }
}
