package com.musinsa.project.application.ranking.event

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

sealed class RankingEvent {
    abstract val brandId: Long

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class ProductRankingRemoveAllEvent(
        @JsonProperty("id")
        override val brandId: Long,
    ) : RankingEvent()

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class ProductRankingRemoveEvent(
        override val brandId: Long,
        val categoryId: Long,
    ) : RankingEvent()

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class ProductRankingUpsertEvent(
        override val brandId: Long,
        val categoryId: Long,
        val beforePrice: BigDecimal?,
        val price: BigDecimal, // After Price
    ) : RankingEvent() {
        @JsonIgnore
        fun priceDiff() = (beforePrice?.minus(price) ?: BigDecimal.ZERO.minus(price)).unaryMinus()
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class ProductRankingChangeEvent(
        override val brandId: Long,
        val categoryId: Long,
        val price: BigDecimal,
    ) : RankingEvent() {
        @JsonIgnore
        fun priceDiff() = price.unaryMinus()
    }
}