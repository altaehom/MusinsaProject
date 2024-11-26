package com.musinsa.project.application.ranking

import com.musinsa.project.application.exception.ApplicationException.BrandNotFoundException
import com.musinsa.project.application.exception.ApplicationException.CategoryNotFoundException
import com.musinsa.project.application.ranking.PriceFormatter.formatted
import com.musinsa.project.application.ranking.model.CategoryPriceRankModel
import com.musinsa.project.application.ranking.model.LowestBrandTotalPriceModel
import com.musinsa.project.domain.service.brand.BrandDomainService
import com.musinsa.project.domain.service.brand.model.BrandModel
import com.musinsa.project.domain.service.category.CategoryDomainService
import com.musinsa.project.domain.service.category.model.CategoryModel
import com.musinsa.project.domain.service.product.ProductDomainService
import com.musinsa.project.domain.service.product.model.ProductModel
import com.musinsa.project.webapi.api.product.model.BrandResponse
import com.musinsa.project.webapi.api.product.model.BrandWiseLowestRankingData
import com.musinsa.project.webapi.api.product.model.BrandWiseLowestRankingResponse
import com.musinsa.project.webapi.api.product.model.CategoryHighLowRankingResponse
import com.musinsa.project.webapi.api.product.model.CategoryResponse
import com.musinsa.project.webapi.api.product.model.CategoryWiseLowestRankingData
import com.musinsa.project.webapi.api.product.model.CategoryWiseLowestRankingResponse
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@ExtendWith(MockKExtension::class)
class RankingQueryServiceTest {
    @InjectMockKs
    private lateinit var mut: RankingQueryService
    private val categoryDomainService: CategoryDomainService = mockk(relaxed = true)
    private val brandDomainService: BrandDomainService = mockk(relaxed = true)
    private val productDomainService: ProductDomainService = mockk(relaxed = true)
    private val rankingDataRetriever: RankingDataRetriever = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `getCategoryWiseLowestRanking_1_응답 모델 반환 테스트`() {
        val categoriesIds = listOf(1L, 2L)
        val ranks =
            categoriesIds.map {
                CategoryPriceRankModel(
                    categoryId = it,
                    brandId = 2,
                    price = BigDecimal.TEN,
                )
            }

        every { categoryDomainService.getAll() }.returns(
            categoriesIds.map {
                CategoryModel(
                    id = it,
                    categoryName = "category$it",
                    createdAt = Instant.now(),
                )
            },
        )
        every { rankingDataRetriever.getLowestPriceRankByCategories(categoriesIds) }.returns(ranks)
        every { brandDomainService.gets(ranks.mapNotNull { it.brandId }) }.returns(
            ranks.map {
                BrandModel(it.brandId!!, "Brand", Instant.now(), null)
            },
        )

        val result = mut.getCategoryWiseLowestRanking()
        val model =
            CategoryWiseLowestRankingResponse(
                items =
                    ranks.map { rank ->
                        CategoryWiseLowestRankingData(
                            categoryName = "category${rank.categoryId}",
                            brandName = "Brand",
                            price = rank.price?.let { formatted(it) },
                        )
                    },
                totalPrice =
                    ranks
                        .mapNotNull { it.price }
                        .sumOf { it }
                        .let { formatted(it) },
            )

        assertEquals(result, model)
    }

    @Test
    fun `getBrandWiseLowestRanking_2_브랜드를 찾지 못하면 에러를 반환한다`() {
        val brandId = 1L

        every { rankingDataRetriever.getLowestBrandTotalPriceRank() }.returns(LowestBrandTotalPriceModel(brandId))
        every { brandDomainService.get(brandId) }.returns(null)

        assertFailsWith<BrandNotFoundException> {
            mut.getBrandWiseLowestRanking()
        }
    }

    @Test
    fun `getBrandWiseLowestRanking_3_카테고리를 찾지 못하면 에러를 반환한다`() {
        val brandId = 1L

        every { rankingDataRetriever.getLowestBrandTotalPriceRank() }.returns(LowestBrandTotalPriceModel(brandId))
        every { brandDomainService.get(brandId) }.returns(BrandModel(brandId, "Brand", Instant.now(), Instant.now()))
        every { categoryDomainService.getAll() }.returns(emptyList())
        every { productDomainService.getByBrandId(brandId) }.returns(
            listOf(
                ProductModel(1L, brandId, categoryId = 1L, price = BigDecimal.TEN, Instant.now(), null),
            ),
        )

        assertFailsWith<CategoryNotFoundException> {
            mut.getBrandWiseLowestRanking()
        }
    }

    @Test
    fun `getBrandWiseLowestRanking_4_응답 모델 반환 테스트`() {
        val brandId = 1L
        val categoryIds = listOf(1L, 2L)
        val products =
            categoryIds.map {
                ProductModel(1L, brandId, categoryId = it, price = BigDecimal.TEN, Instant.now(), null)
            }
        val categories = categoryIds.map { CategoryModel(it, "category$it", Instant.now()) }
        val categoriesMap = categories.associateBy { it.id }

        every { rankingDataRetriever.getLowestBrandTotalPriceRank() }.returns(LowestBrandTotalPriceModel(brandId))
        every { brandDomainService.get(brandId) }.returns(BrandModel(brandId, "Brand", Instant.now(), Instant.now()))
        every { categoryDomainService.getAll() }.returns(categories)
        every { productDomainService.getByBrandId(brandId) }.returns(products)

        val result = mut.getBrandWiseLowestRanking()
        val model =
            BrandWiseLowestRankingResponse(
                item =
                    BrandWiseLowestRankingData(
                        categories =
                            products.map {
                                CategoryResponse(
                                    categoryName = categoriesMap[it.categoryId]?.categoryName ?: throw CategoryNotFoundException(),
                                    price = formatted(it.price),
                                )
                            },
                        brandName = "Brand",
                        totalPrice =
                            products
                                .sumOf { it.price }
                                .let { formatted(it) },
                    ),
            )

        assertEquals(result, model)
    }

    @Test
    fun `getCategoryHighLowRankings_1_카테고리를 조회 할 수 없으면 에러를 반환한다`() {
        val categoryName = "테스트"

        every { categoryDomainService.getAll() }.returns(emptyList())

        assertFailsWith<CategoryNotFoundException> {
            mut.getCategoryHighLowRankings(categoryName)
        }
    }

    @Test
    fun `getCategoryHighLowRankings_2_카테고리를 조회 할 수 없으면 에러를 반환한다`() {
        val categoryName = "테스트"

        every { categoryDomainService.getAll() }.returns(
            listOf(
                CategoryModel(1L, "NoNo", Instant.now()),
            ),
        )

        assertFailsWith<CategoryNotFoundException> {
            mut.getCategoryHighLowRankings(categoryName)
        }
    }

    @Test
    fun `getCategoryHighLowRankings_3_응답 모델 반환 테스트`() {
        val categoryName = "테스트"
        val categoryModel = CategoryModel(1L, categoryName, Instant.now())
        val brandIds = listOf(1L, 2L)
        val lowest =
            CategoryPriceRankModel(
                categoryId = categoryModel.id,
                brandId = brandIds.first(),
                price = brandIds.first().toBigDecimal() * BigDecimal.TEN,
            )
        val highest =
            CategoryPriceRankModel(
                categoryId = categoryModel.id,
                brandId = brandIds.last(),
                price = brandIds.last().toBigDecimal() * BigDecimal.TEN,
            )
        val brands = brandIds.map { BrandModel(it, "Brand$it", Instant.now(), null) }

        every { categoryDomainService.getAll() }.returns(listOf(categoryModel))
        every { rankingDataRetriever.getCategoryPriceRankAsc(categoryModel.id) }.returns(lowest)
        every { rankingDataRetriever.getCategoryPriceRankDesc(categoryModel.id) }.returns(highest)
        every { brandDomainService.gets(brandIds) }.returns(brands)

        val result = mut.getCategoryHighLowRankings(categoryName)
        val model =
            CategoryHighLowRankingResponse(
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

        assertEquals(result, model)
    }

    @Test
    fun `getCategoryHighLowRankings_4_브랜드를 찾지 못하면 에러를 반환한다`() {
        val categoryName = "테스트"
        val categoryModel = CategoryModel(1L, categoryName, Instant.now())
        val brandIds = listOf(1L, 2L)
        val lowest =
            CategoryPriceRankModel(
                categoryId = categoryModel.id,
                brandId = brandIds.first(),
                price = brandIds.first().toBigDecimal() * BigDecimal.TEN,
            )
        val highest =
            CategoryPriceRankModel(
                categoryId = categoryModel.id,
                brandId = brandIds.last(),
                price = brandIds.last().toBigDecimal() * BigDecimal.TEN,
            )

        every { categoryDomainService.getAll() }.returns(listOf(categoryModel))
        every { rankingDataRetriever.getCategoryPriceRankAsc(categoryModel.id) }.returns(lowest)
        every { rankingDataRetriever.getCategoryPriceRankDesc(categoryModel.id) }.returns(highest)
        every { brandDomainService.gets(brandIds) }.returns(emptyList())

        assertFailsWith<BrandNotFoundException> {
            mut.getCategoryHighLowRankings(categoryName)
        }
    }
}
