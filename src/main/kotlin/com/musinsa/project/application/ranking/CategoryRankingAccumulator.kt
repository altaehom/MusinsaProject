package com.musinsa.project.application.ranking

import com.musinsa.project.application.ranking.RankingConstants.RANKING_TTL_HOURS
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class CategoryRankingAccumulator(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    fun accumulate(
        score: BigDecimal,
        categoryId: Long,
        value: String, // 브랜드 ㅑㅇ
    ) {
        val key = makeKey(categoryId)
        redisTemplate.executePipelined { conn ->
            conn.openPipeline()
            redisTemplate.opsForZSet().add(
                key,
                value,
                score.toDouble(),
            )
            redisTemplate.expire(key, RANKING_TTL_HOURS)
            conn.closePipeline()
            null
        }
    }

    fun remove(
        categoryId: Long,
        value: String, // 브랜드
    ) {
        val key = makeKey(categoryId)
        val score = redisTemplate.opsForZSet().score(key, value) ?: return

        redisTemplate.executePipelined { conn ->
            conn.openPipeline()
            redisTemplate.opsForZSet().remove(key, value)
            redisTemplate.expire(key, RANKING_TTL_HOURS)
            conn.closePipeline()
            null
        }
    }

    companion object {
        private fun makeKey(categoryId: Long): String = "CATEGORY::$categoryId"
    }
}
