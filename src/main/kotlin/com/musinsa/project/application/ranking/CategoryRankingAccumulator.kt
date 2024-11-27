package com.musinsa.project.application.ranking

import com.musinsa.project.application.ranking.RankingConstants.RANKING_TTL_HOURS
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.math.BigDecimal

/**
 * 전달 된 랭킹 반영 이벤트를 zset에 저장하는 클래스
 * zset은 카테고리 별로 존재
 */
@Component
class CategoryRankingAccumulator(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    /**
     * 카테고리 id를 기준으로 Zset이 생성
     * Value 값은, ProductId:BrandId 형식으로, 동일한 브랜드를 가진 N개의 제품이 존재
     * Score는 변경 된 가격으로 덮어써짐
     */
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
