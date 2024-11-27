package com.musinsa.project.application.ranking.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.musinsa.project.application.ranking.event.RankingEvent.BrandRankingRemoveAllEvent
import com.musinsa.project.application.ranking.event.RankingEvent.ProductRankingChangeEvent
import com.musinsa.project.application.ranking.event.RankingEvent.ProductRankingRemoveEvent
import com.musinsa.project.application.ranking.event.RankingEvent.ProductRankingUpsertEvent
import com.musinsa.project.application.ranking.event.RankingEventType.BRAND_DELETED_EVENT
import com.musinsa.project.application.ranking.event.RankingEventType.PRODUCT_CREATED_EVENT
import com.musinsa.project.application.ranking.event.RankingEventType.PRODUCT_DELETED_EVENT
import com.musinsa.project.application.ranking.event.RankingEventType.PRODUCT_REMOVED_EVENT
import com.musinsa.project.application.ranking.event.RankingEventType.PRODUCT_UPDATED_EVENT
import org.springframework.stereotype.Component

@Component
class RankingEventConverter(
    private val objectMapper: ObjectMapper,
) {
    fun convertEvent(
        type: RankingEventType,
        payload: String,
    ): RankingEvent =
        when (type) {
            PRODUCT_CREATED_EVENT, PRODUCT_UPDATED_EVENT -> {
                objectMapper.readValue<ProductRankingUpsertEvent>(payload)
            }
            PRODUCT_REMOVED_EVENT -> {
                objectMapper.readValue<ProductRankingChangeEvent>(payload)
            }
            BRAND_DELETED_EVENT -> {
                objectMapper.readValue<BrandRankingRemoveAllEvent>(payload)
            }
            PRODUCT_DELETED_EVENT -> {
                objectMapper.readValue<ProductRankingRemoveEvent>(payload)
            }
            else -> {
                throw IllegalArgumentException("Unknown Event!")
            }
        }
}
