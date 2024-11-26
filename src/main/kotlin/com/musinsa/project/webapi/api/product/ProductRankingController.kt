package com.musinsa.project.webapi.api.product

import com.musinsa.project.application.ranking.RankingQueryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/ranking")
class ProductRankingController(
    private val rankingQueryService: RankingQueryService,
) {
    @GetMapping(path = ["/category/lowest"])
    fun getCategoryWiseLowestRanking() = rankingQueryService.getCategoryWiseLowestRanking()

    @GetMapping(path = ["/brand/lowest"])
    fun getBrandWiseLowestRanking() = rankingQueryService.getBrandWiseLowestRanking()

    @PostMapping(path = ["/category"])
    fun getCategoryHighLowRankings(
        @RequestParam categoryName: String,
    ) = rankingQueryService.getCategoryHighLowRankings(categoryName)
}
