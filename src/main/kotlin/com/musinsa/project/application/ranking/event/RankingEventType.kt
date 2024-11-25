package com.musinsa.project.application.ranking.event

enum class RankingEventType(
    val eventName: String,
) {
    BRAND_DELETED_EVENT("BrandDeletedEvent"),
    PRODUCT_CREATED_EVENT("ProductCreatedEvent"),
    PRODUCT_UPDATED_EVENT("ProductUpdatedEvent"),
    PRODUCT_REMOVED_EVENT("ProductRemovedEvent"),
    PRODUCT_DELETED_EVENT("ProductDeletedEvent"),
    UNKNOWN("UnknownEvent"),
    ;

    fun isUnknownEvent() = this == UNKNOWN

    companion object {
        private val TYPES = entries.toTypedArray()

        fun getType(eventName: String) = TYPES.find { it.eventName == eventName } ?: UNKNOWN
    }
}
