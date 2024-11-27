package com.musinsa.project.application.outbox

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.redis.core.RedisTemplate
import java.time.Duration
import java.util.UUID
import kotlin.test.assertTrue

@ExtendWith(MockKExtension::class)
class EventDuplicateCheckerTest {
    @InjectMockKs
    private lateinit var mut: EventDuplicateChecker
    private val redisTemplate: RedisTemplate<String, String> = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `isDuplicate_1_이미 값이 존재한다면 true를 반환한다`() {
        every { redisTemplate.opsForValue().get(any()) }.returns("aaa")

        assertTrue(mut.isDuplicate("aa"))
    }

    @Test
    fun `isDuplicate_2_값이 없다면 false를 반환한다`() {
        every { redisTemplate.opsForValue().get(any()) }.returns(null)

        assertTrue(mut.isDuplicate("aa").not())
    }

    @Test
    fun `mark_1_레디스의 값이 반영 된다`() {
        val eventId = UUID.randomUUID().toString()
        mut.mark(eventId)

        verify { redisTemplate.opsForValue().set("EVENT::$eventId", eventId, Duration.ofDays(1)) }
    }
}
