package com.musinsa.project.application.ranking

import com.musinsa.project.application.ranking.RankingConstants.RANKING_TTL_HOURS
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.connection.RedisZSetCommands
import org.springframework.data.redis.core.RedisCallback
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.math.BigDecimal

@ExtendWith(MockKExtension::class)
class CategoryRankingAccumulatorTest {
    @InjectMockKs
    private lateinit var mut: CategoryRankingAccumulator
    private val redisTemplate: RedisTemplate<String, String> = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `accumulate_1_레디스 파이프라인을 이용해서, zadd와 expire가 실행 된다`() {
        val categoryId = 1L
        val value = "2"
        val key = "CATEGORY::$categoryId"
        val redisConnection: RedisConnection = mockk(relaxed = true)
        val redisZSetCommands: RedisZSetCommands = mockk(relaxed = true)
        val stringSerializer = StringRedisSerializer()
        val serializedKey = stringSerializer.serialize(key)
        val serializedValue = stringSerializer.serialize(value)

        every { redisConnection.zSetCommands() } returns redisZSetCommands
        every { redisTemplate.stringSerializer }.returns(stringSerializer)
        every { redisZSetCommands.zAdd(serializedKey, any(), serializedValue) }.returns(true)
        every { redisConnection.expire(any(), any()) } returns true
        every { redisTemplate.executePipelined(any<RedisCallback<Any>>()) } answers {
            val callback = firstArg<RedisCallback<Any>>()
            callback.doInRedis(redisConnection)
            emptyList()
        }

        mut.accumulate(
            score = BigDecimal.TEN,
            categoryId = categoryId,
            value = value,
        )

        verifyOrder {
            redisTemplate.executePipelined(any<RedisCallback<Any>>())
            redisZSetCommands.zAdd(
                serializedKey,
                BigDecimal.TEN.toDouble(),
                serializedValue,
            )
            redisConnection.expire(
                serializedKey,
                RANKING_TTL_HOURS.seconds,
            )
        }
    }

    @Test
    fun `remove_1_키가 조회 되지 않으면 return 된다`() {
        val categoryId = 1L
        val value = "2"
        val key = "CATEGORY::$categoryId"

        every { redisTemplate.opsForZSet().score(key, value) }.returns(null)

        mut.remove(
            categoryId = categoryId,
            value = value,
        )

        verify(exactly = 0) { redisTemplate.executePipelined(any<RedisCallback<Any>>()) }
    }

    @Test
    fun `remove_2_레디스 파이프라인을 이용해서, zRem와 expire가 실행 된다`() {
        val categoryId = 1L
        val value = "2"
        val key = "CATEGORY::$categoryId"
        val redisConnection: RedisConnection = mockk(relaxed = true)
        val redisZSetCommands: RedisZSetCommands = mockk(relaxed = true)
        val stringSerializer = StringRedisSerializer()
        val serializedKey = stringSerializer.serialize(key)
        val serializedValue = stringSerializer.serialize(value)

        every { redisTemplate.opsForZSet().score(key, value) }.returns(0.0)
        every { redisConnection.zSetCommands() } returns redisZSetCommands
        every { redisTemplate.stringSerializer }.returns(stringSerializer)
        every { redisZSetCommands.zRem(serializedKey, serializedValue) }.returns(0)
        every { redisConnection.expire(any(), any()) } returns true
        every { redisTemplate.executePipelined(any<RedisCallback<Any>>()) } answers {
            val callback = firstArg<RedisCallback<Any>>()
            callback.doInRedis(redisConnection)
            emptyList()
        }

        mut.remove(
            categoryId = categoryId,
            value = value,
        )

        verifyOrder {
            redisTemplate.executePipelined(any<RedisCallback<Any>>())
            redisZSetCommands.zRem(
                serializedKey,
                serializedValue,
            )
            redisConnection.expire(
                serializedKey,
                RANKING_TTL_HOURS.seconds,
            )
        }
    }
}
