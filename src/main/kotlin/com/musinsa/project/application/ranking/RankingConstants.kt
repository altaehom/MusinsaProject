package com.musinsa.project.application.ranking

import java.time.Duration

object RankingConstants {
    val RANKING_TTL_HOURS = Duration.ofHours(1)

    fun getCategoryRankingValueName(
        productId: Long,
        brandId: Long,
    ) = "$productId-$brandId"

    fun Any.getCategoryRankingValueBrandId() =
        this
            .toString()
            .split("-")
            .last()
            .toLong()
}
