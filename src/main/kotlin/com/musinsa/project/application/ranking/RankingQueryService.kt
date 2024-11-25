package com.musinsa.project.application.ranking

import com.musinsa.project.application.ranking.PriceFormatter.formatted
import com.musinsa.project.domain.service.brand.BrandDomainService
import com.musinsa.project.domain.service.category.CategoryDomainService
import com.musinsa.project.webapi.api.product.model.ProductLowestPriceRankingResponse
import com.musinsa.project.webapi.api.product.model.ProductLowestPriceRankingResponses
import org.springframework.stereotype.Service

@Service
class RankingQueryService(
    private val categoryDomainService: CategoryDomainService,
    private val brandDomainService: BrandDomainService,
    private val rankingDataRetriever: RakningDataRetriever,
) {
    fun getLowestPriceRankByCategory(): ProductLowestPriceRankingResponses {
        val categories =
            categoryDomainService
                .getAll()
                .associateBy { it.id }
        val categoryIds = categories.values.map { it.id }.sortedBy { it }
        val ranks = rankingDataRetriever.getLowestPriceRankByCategory(categoryIds)
        val brandMaps = brandDomainService.gets(ranks.mapNotNull { it.brandId }).associateBy { it.id }

        return ProductLowestPriceRankingResponses(
            items =
                ranks.map { rank ->
                    ProductLowestPriceRankingResponse(
                        categoryName = categories.getValue(rank.categoryId).categoryName,
                        brandName = brandMaps[rank.brandId]?.brandName,
                        price = rank.price?.let { formatted(it) },
                    )
                },
            totalPrice =
                ranks
                    .mapNotNull { it.price }
                    .sumOf { it }
                    .let { formatted(it) },
        )
    }
}
