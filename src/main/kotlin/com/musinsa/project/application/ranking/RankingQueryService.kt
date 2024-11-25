package com.musinsa.project.application.ranking

import com.musinsa.project.application.exception.ApplicationException.BrandNotFoundException
import com.musinsa.project.application.exception.ApplicationException.CategoryNotFoundException
import com.musinsa.project.application.exception.ApplicationException.RankingNotFoundException
import com.musinsa.project.application.ranking.PriceFormatter.formatted
import com.musinsa.project.domain.service.brand.BrandDomainService
import com.musinsa.project.domain.service.category.CategoryDomainService
import com.musinsa.project.domain.service.product.ProductDomainService
import com.musinsa.project.webapi.api.product.model.BrandLowestPriceRankingModel
import com.musinsa.project.webapi.api.product.model.BrandLowestPriceRankingResponse
import com.musinsa.project.webapi.api.product.model.CategoryResponse
import com.musinsa.project.webapi.api.product.model.ProductLowestPriceRankingResponse
import com.musinsa.project.webapi.api.product.model.ProductLowestPriceRankingResponses
import org.springframework.stereotype.Service

@Service
class RankingQueryService(
    private val categoryDomainService: CategoryDomainService,
    private val brandDomainService: BrandDomainService,
    private val productDomainService: ProductDomainService,
    private val rankingDataRetriever: RankingDataRetriever,
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

    fun lowestBrandPriceTotal(): BrandLowestPriceRankingResponse {
        val rank = rankingDataRetriever.getLowestBrandTotalPriceRank() ?: throw RankingNotFoundException()
        val brand = brandDomainService.get(rank.brandId) ?: throw BrandNotFoundException()
        val categories = categoryDomainService.getAll().associateBy { it.id }
        val products = productDomainService.getByBrandId(rank.brandId)

        return BrandLowestPriceRankingResponse(
            item =
                BrandLowestPriceRankingModel(
                    categories =
                        products.map {
                            CategoryResponse(
                                categoryName = categories[it.categoryId]?.categoryName ?: throw CategoryNotFoundException(),
                                price = formatted(it.price),
                            )
                        },
                    brandName = brand.brandName,
                    totalPrice =
                        products
                            .sumOf { it.price }
                            .let { formatted(it) },
                ),
        )
    }
}
