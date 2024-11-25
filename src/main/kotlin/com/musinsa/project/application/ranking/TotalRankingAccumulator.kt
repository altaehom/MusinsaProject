package com.musinsa.project.application.ranking

import com.musinsa.project.application.ranking.RankingConstants.RANKING_TTL_HOURS
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class TotalRankingAccumulator(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    fun accumulate(
        score: BigDecimal,
        value: String, // 브랜드 id
    ) {
        redisTemplate.executePipelined { conn ->
            conn.openPipeline()

            redisTemplate.opsForZSet().incrementScore(
                KEY_NAME,
                value,
                score.toDouble(),
            )
            redisTemplate.expire(KEY_NAME, RANKING_TTL_HOURS)

            conn.closePipeline()
            null
        }
    }

    fun remove(value: String) { // 브랜드 id
        val score = redisTemplate.opsForZSet().score(KEY_NAME, value) ?: return
        redisTemplate.executePipelined { conn ->
            conn.openPipeline()
            redisTemplate.opsForZSet().remove(KEY_NAME, value)
            redisTemplate.expire(KEY_NAME, RANKING_TTL_HOURS)
            conn.closePipeline()
            null
        }
    }

    companion object {
        private val KEY_NAME = "TOTAL::Brand"
    }
}
