package com.musinsa.project.webapi.api.product

import com.musinsa.project.application.ranking.RankingQueryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/ranking")
class ProductRankingController(
    private val rankingQueryService: RankingQueryService,
) {
    @GetMapping(path = ["/category/lowest"])
    fun lowestProductByCategory() = rankingQueryService.getLowestPriceRankByCategory()
}
