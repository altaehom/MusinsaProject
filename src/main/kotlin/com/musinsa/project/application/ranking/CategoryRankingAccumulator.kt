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
        value: String, // 브랜드 Id
    ) {
        val key = makeCategoryRankingKey(categoryId)
        redisTemplate.executePipelined { conn ->
            val stringSerializer = redisTemplate.stringSerializer
            val serializeKey = stringSerializer.serialize(key)!!

            conn.zSetCommands().zAdd(
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

    fun remove(
        categoryId: Long,
        value: String, // 브랜드 Id
    ) {
        val key = makeCategoryRankingKey(categoryId)
        val score = redisTemplate.opsForZSet().score(key, value) ?: return

        redisTemplate.executePipelined { conn ->
            val stringSerializer = redisTemplate.stringSerializer
            val serializeKey = stringSerializer.serialize(key)!!

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
        fun makeCategoryRankingKey(categoryId: Long): String = "CATEGORY::$categoryId"
    }
}
