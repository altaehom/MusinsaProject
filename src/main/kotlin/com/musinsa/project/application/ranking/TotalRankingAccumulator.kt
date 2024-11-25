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
            val stringSerializer = redisTemplate.stringSerializer
            val serializeKey = stringSerializer.serialize(TOTAL_BRAND_RANKING_KEY_NAME)!!

            conn.zSetCommands().zIncrBy(
                serializeKey,
                score.toDouble(),
                stringSerializer.serialize(value)!!,
            )

            conn.expire(
                serializeKey,
                RANKING_TTL_HOURS.toSeconds(),
            )

            null
        }
    }

    fun remove(value: String) { // 브랜드 id
        val score = redisTemplate.opsForZSet().score(TOTAL_BRAND_RANKING_KEY_NAME, value) ?: return
        redisTemplate.executePipelined { conn ->
            val stringSerializer = redisTemplate.stringSerializer
            val serializeKey = stringSerializer.serialize(TOTAL_BRAND_RANKING_KEY_NAME)!!

            conn.zSetCommands().zRem(
                serializeKey,
                stringSerializer.serialize(value)!!,
            )

            conn.expire(
                serializeKey,
                RANKING_TTL_HOURS.toSeconds(),
            )

            null
        }
    }

    companion object {
        val TOTAL_BRAND_RANKING_KEY_NAME = "TOTAL::Brand"
    }
}
