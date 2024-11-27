package com.musinsa.project.application.outbox

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

/**
 * 이벤트 반영이 멱등하게 처리 되었음을 확인하는 클래스
 * */
@Component
class EventDuplicateChecker(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    fun isDuplicate(eventId: String) = redisTemplate.opsForValue().get("$KEY_PREFIX$eventId")?.let { true } ?: false

    fun mark(eventId: String) = redisTemplate.opsForValue().set("$KEY_PREFIX$eventId", eventId, TTL)

    companion object {
        private const val KEY_PREFIX = "EVENT::"
        private val TTL = Duration.ofDays(1)
    }
}
