package com.musinsa.project.application.ranking

import com.musinsa.project.application.exception.ApplicationException.BrandNotFoundException
import com.musinsa.project.application.exception.ApplicationException.CategoryNotFoundException
import com.musinsa.project.application.ranking.PriceFormatter.formatted
import com.musinsa.project.domain.service.brand.BrandDomainService
import com.musinsa.project.domain.service.category.CategoryDomainService
import com.musinsa.project.domain.service.product.ProductDomainService
import com.musinsa.project.webapi.api.product.model.BrandResponse
import com.musinsa.project.webapi.api.product.model.BrandWiseLowestRankingData
import com.musinsa.project.webapi.api.product.model.BrandWiseLowestRankingResponse
import com.musinsa.project.webapi.api.product.model.CategoryHighLowRankingResponse
import com.musinsa.project.webapi.api.product.model.CategoryResponse
import com.musinsa.project.webapi.api.product.model.CategoryWiseLowestRankingData
import com.musinsa.project.webapi.api.product.model.CategoryWiseLowestRankingResponse
import org.springframework.stereotype.Service

@Service
class RankingQueryService(
    private val categoryDomainService: CategoryDomainService,
    private val brandDomainService: BrandDomainService,
    private val productDomainService: ProductDomainService,
    private val rankingDataRetriever: RankingDataRetriever,
) {
    fun getCategoryWiseLowestRanking(): CategoryWiseLowestRankingResponse {
        val categories =
            categoryDomainService
                .getAll()
                .associateBy { it.id }
        val categoryIds = categories.values.map { it.id }.sortedBy { it }
        val ranks = rankingDataRetriever.getLowestPriceRankByCategories(categoryIds)
        val brandMaps = brandDomainService.gets(ranks.mapNotNull { it.brandId }).associateBy { it.id }

        return CategoryWiseLowestRankingResponse(
            items =
                ranks.map { rank ->
                    CategoryWiseLowestRankingData(
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

    fun getBrandWiseLowestRanking(): BrandWiseLowestRankingResponse {
        val rank = rankingDataRetriever.getLowestBrandTotalPriceRank()
        val brand = brandDomainService.get(rank.brandId) ?: throw BrandNotFoundException()
        val categories = categoryDomainService.getAll().associateBy { it.id }
        val products =
            productDomainService
                .getByBrandId(rank.brandId)
                .groupBy { it.categoryId }
                .map { it.value.minByOrNull { it.price } }
                .filterNotNull()

        return BrandWiseLowestRankingResponse(
            item =
                BrandWiseLowestRankingData(
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

    fun getCategoryHighLowRankings(categoryName: String): CategoryHighLowRankingResponse {
        val category =
            categoryDomainService
                .getAll()
                .find { it.categoryName == categoryName }
                ?: throw CategoryNotFoundException()
        val lowest = rankingDataRetriever.getCategoryPriceRankAsc(category.id)
        val highest = rankingDataRetriever.getCategoryPriceRankDesc(category.id)
        val brands = brandDomainService.gets(listOfNotNull(lowest.brandId, highest.brandId))

        return CategoryHighLowRankingResponse(
            categoryName = categoryName,
            lowest =
                lowest.price?.let { price ->
                    brands.find { it.id == lowest.brandId }?.let {
                        BrandResponse(
                            brandName = it.brandName,
                            price = formatted(price),
                        )
                    } ?: throw BrandNotFoundException()
                },
            highest =
                highest.price?.let { price ->
                    brands.find { it.id == highest.brandId }?.let {
                        BrandResponse(
                            brandName = it.brandName,
                            price = formatted(price),
                        )
                    } ?: throw BrandNotFoundException()
                },
        )
    }
}
