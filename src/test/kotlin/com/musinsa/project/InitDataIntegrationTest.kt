package com.musinsa.project

import com.musinsa.project.domain.service.brand.BrandDomainService
import com.musinsa.project.domain.service.category.CategoryDomainService
import com.musinsa.project.domain.service.outbox.OutboxDomainService
import com.musinsa.project.domain.service.product.ProductDomainService
import com.musinsa.project.webapi.api.brand.model.CreateBrandAdminRequest
import com.musinsa.project.webapi.api.brand.model.UpdateBrandAdminRequest
import com.musinsa.project.webapi.api.product.model.CreateProductAdminRequest
import com.musinsa.project.webapi.api.product.model.UpdateProductAdminRequest
import com.musinsa.project.webapi.api.utils.ItTestUtils.createURLWithPort
import io.mockk.clearAllMocks
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.Ordered.LOWEST_PRECEDENCE
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.math.BigDecimal

@ExtendWith(SpringExtension::class)
@SpringBootTest(
    classes = [MusinsaProjectApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class InitDataIntegrationTest {
    @LocalServerPort private val port = 0

    @Autowired private lateinit var brandDomainService: BrandDomainService

    @Autowired private lateinit var categoryDomainService: CategoryDomainService

    @Autowired private lateinit var productDomainService: ProductDomainService

    @Autowired private lateinit var outboxDomainService: OutboxDomainService

    @Autowired private lateinit var redisTemplate: RedisTemplate<String, String>

    private val restTemplate: TestRestTemplate = TestRestTemplate()
    private val headers: HttpHeaders = HttpHeaders().apply { this.contentType = MediaType.APPLICATION_JSON }

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        Thread.sleep(300)
    }

    @Test
    @Order(HIGHEST_PRECEDENCE)
    fun init() {
        outboxDomainService.clear()
        redisTemplate.execute {
            it.flushAll()
        }
        brandDomainService.clear()
        categoryDomainService.clear()
        productDomainService.clear()
    }

    @Order(0)
    @ParameterizedTest
    @CsvSource(
        value = ["A", "B", "C", "D", "E", "F", "G", "H", "I"],
    )
    fun createBrands(brandName: String) {
        brandDomainService.save(brandName)
    }

    @Order(0)
    @ParameterizedTest
    @CsvSource(
        value = ["상의", "아우터", "바지", "스니커즈", "가방", "모자", "양말", "액세서리"],
    )
    fun createCategory(categoryName: String) {
        categoryDomainService.save(categoryName)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "A, 상의, 11200", "A, 아우터, 5500", "A, 바지, 4200", "A, 스니커즈, 9000",
            "A, 가방, 2000", "A, 모자, 1700", "A, 양말, 1800", "A, 액세서리, 2300",
            "B, 상의, 10500", "B, 아우터, 5900", "B, 바지, 3800", "B, 스니커즈, 9100",
            "B, 가방, 2100", "B, 모자, 2000", "B, 양말, 2000", "B, 액세서리, 2200",
            "C, 상의, 10000", "C, 아우터, 6200", "C, 바지, 3300", "C, 스니커즈, 9200",
            "C, 가방, 2200", "C, 모자, 1900", "C, 양말, 2200", "C, 액세서리, 2100",
            "D, 상의, 10100", "D, 아우터, 5100", "D, 바지, 3000", "D, 스니커즈, 9500",
            "D, 가방, 2500", "D, 모자, 1500", "D, 양말, 2400", "D, 액세서리, 2000",
            "E, 상의, 10700", "E, 아우터, 5000", "E, 바지, 3800", "E, 스니커즈, 9900",
            "E, 가방, 2300", "E, 모자, 1800", "E, 양말, 2100", "E, 액세서리, 2100",
            "F, 상의, 11200", "F, 아우터, 7200", "F, 바지, 4000", "F, 스니커즈, 9300",
            "F, 가방, 2100", "F, 모자, 1600", "F, 양말, 2300", "F, 액세서리, 1900",
            "G, 상의, 10500", "G, 아우터, 5800", "G, 바지, 3900", "G, 스니커즈, 9000",
            "G, 가방, 2200", "G, 모자, 1700", "G, 양말, 2100", "G, 액세서리, 2000",
            "H, 상의, 10800", "H, 아우터, 6300", "H, 바지, 3100", "H, 스니커즈, 9700",
            "H, 가방, 2100", "H, 모자, 1600", "H, 양말, 2000", "H, 액세서리, 2000",
            "I, 상의, 11400", "I, 아우터, 6700", "I, 바지, 3200", "I, 스니커즈, 9500",
            "I, 가방, 2400", "I, 모자, 1700", "I, 양말, 1700", "I, 액세서리, 2400",
        ],
    )
    @Order(1)
    fun createProducts(
        brandName: String,
        categoryName: String,
        price: BigDecimal,
    ) {
        val brand = brandDomainService.getByBrandName(brandName)
        val category = categoryDomainService.getByCategoryName(categoryName)
        productDomainService.save(
            brandId = brand.id,
            categoryId = category.id,
            price = price,
        )
    }

    @Test
    @Order(LOWEST_PRECEDENCE)
    fun `getCategoryWiseLowestRanking_호출 테스트`() {
        val response =
            restTemplate.exchange<String>(
                createURLWithPort("$RANKING_API_BASE$CATEGORY_WISE_LOWEST", port),
                HttpMethod.GET,
                HttpEntity(null, headers),
            )

        assertEquals(response.statusCode, HttpStatus.OK)
        assertEquals(response.body.toString(), CATEGORY_WISE_LOWEST_RESULT)
    }

    @Test
    @Order(LOWEST_PRECEDENCE)
    fun `getBrandWiseLowestRanking_호출 테스트`() {
        val response =
            restTemplate.exchange<String>(
                createURLWithPort("$RANKING_API_BASE$BRAND_WISE_LOWEST", port),
                HttpMethod.GET,
                HttpEntity(null, headers),
            )

        assertEquals(response.statusCode, HttpStatus.OK)
        assertEquals(response.body, BRAND_WISE_LOWEST_RESULT)
    }

    @Test
    @Order(LOWEST_PRECEDENCE)
    fun `getCategoryHighLowRankings_호출 테스트`() {
        val response =
            restTemplate.exchange<String>(
                createURLWithPort("$RANKING_API_BASE$CATEGORY_HIGH_LOW?categoryName=상의", port),
                HttpMethod.POST,
                HttpEntity(null, headers),
            )

        assertEquals(response.statusCode, HttpStatus.OK)
        assertEquals(response.body, CATEGORY_HIGH_LOW_RESULT)
    }

    @Test
    @Order(2)
    fun `신규 브랜드 상품을 등록,수정,삭제 랭킹 반영 테스트`() {
        val brandName = "TestBrand"
        val categoies = categoryDomainService.getAll()
        val topCategory = categoies.find { it.categoryName == "상의" }!!
        restTemplate
            .exchange<String>(
                createURLWithPort(BRAND_API, port),
                HttpMethod.POST,
                HttpEntity(CreateBrandAdminRequest(brandName), headers),
            ).also { assertEquals(it.statusCode, HttpStatus.OK) }
        val brand =
            brandDomainService
                .getByBrandName(brandName)
                .apply { kotlin.test.assertEquals(brandName, this.brandName) }
        val newBrandName = "NewTestBrand"
        restTemplate.exchange<String>(
            createURLWithPort("$BRAND_API/${brand.id}", port),
            HttpMethod.PUT,
            HttpEntity(UpdateBrandAdminRequest(newBrandName), headers),
        )
        brandDomainService
            .get(brand.id)
            .apply { kotlin.test.assertEquals(newBrandName, this!!.brandName) }

        restTemplate.exchange<String>(
            createURLWithPort("$PRODUCT_API", port),
            HttpMethod.POST,
            HttpEntity(
                CreateProductAdminRequest(
                    brandId = brand.id,
                    categoryId = topCategory.id,
                    price = BigDecimal.valueOf(100),
                ),
                headers,
            ),
        )
        restTemplate.exchange<String>(
            createURLWithPort("$PRODUCT_API", port),
            HttpMethod.POST,
            HttpEntity(
                CreateProductAdminRequest(
                    brandId = brand.id,
                    categoryId = topCategory.id,
                    price = BigDecimal.valueOf(200000),
                ),
                headers,
            ),
        )

        Thread.sleep(3000)

        restTemplate
            .exchange<String>(
                createURLWithPort("$RANKING_API_BASE$CATEGORY_HIGH_LOW?categoryName=상의", port),
                HttpMethod.POST,
                HttpEntity(null, headers),
            ).apply {
                kotlin.test.assertEquals(
                    this.body,
                    """{"카테고리":"상의","최저가":{"브랜드":"NewTestBrand","가격":"100"},"최고가":{"브랜드":"NewTestBrand","가격":"200,000"}}""",
                )
            }

        val products = productDomainService.getByBrandId(brand.id).sortedByDescending { it.price }

        restTemplate.exchange<String>(
            createURLWithPort("$PRODUCT_API/${products.first().id}", port),
            HttpMethod.PUT,
            HttpEntity(
                UpdateProductAdminRequest(
                    price = BigDecimal.TEN,
                ),
                headers,
            ),
        )

        Thread.sleep(3000)

        restTemplate
            .exchange<String>(
                createURLWithPort("$RANKING_API_BASE$BRAND_WISE_LOWEST", port),
                HttpMethod.GET,
                HttpEntity(null, headers),
            ).apply {
                kotlin.test.assertEquals(
                    this.body,
                    """{"최저가":{"카테고리":[{"카테고리":"상의","가격":"10"}],"브랜드":"NewTestBrand","총액":"10"}}""",
                )
            }
        restTemplate
            .exchange<String>(
                createURLWithPort("$RANKING_API_BASE$CATEGORY_WISE_LOWEST", port),
                HttpMethod.GET,
                HttpEntity(null, headers),
            ).apply {
                kotlin.test.assertEquals(
                    this.body,
                    """{"items":[{"카테고리":"상의","브랜드":"NewTestBrand","가격":"10"},{"카테고리":"아우터","브랜드":"E","가격":"5,000"},{"카테고리":"바지","브랜드":"D","가격":"3,000"},{"카테고리":"스니커즈","브랜드":"G","가격":"9,000"},{"카테고리":"가방","브랜드":"A","가격":"2,000"},{"카테고리":"모자","브랜드":"D","가격":"1,500"},{"카테고리":"양말","브랜드":"I","가격":"1,700"},{"카테고리":"액세서리","브랜드":"F","가격":"1,900"}],"총액":"24,110"}""",
                )
            }

        restTemplate.exchange<String>(
            createURLWithPort("$PRODUCT_API/${products.first().id}", port),
            HttpMethod.DELETE,
            HttpEntity(
                null,
                headers,
            ),
        )

        Thread.sleep(3000)

        restTemplate
            .exchange<String>(
                createURLWithPort("$RANKING_API_BASE$CATEGORY_WISE_LOWEST", port),
                HttpMethod.GET,
                HttpEntity(null, headers),
            ).apply {
                kotlin.test.assertEquals(
                    this.body,
                    """{"items":[{"카테고리":"상의","브랜드":"NewTestBrand","가격":"100"},{"카테고리":"아우터","브랜드":"E","가격":"5,000"},{"카테고리":"바지","브랜드":"D","가격":"3,000"},{"카테고리":"스니커즈","브랜드":"G","가격":"9,000"},{"카테고리":"가방","브랜드":"A","가격":"2,000"},{"카테고리":"모자","브랜드":"D","가격":"1,500"},{"카테고리":"양말","브랜드":"I","가격":"1,700"},{"카테고리":"액세서리","브랜드":"F","가격":"1,900"}],"총액":"24,200"}""",
                )
            }

        restTemplate.exchange<String>(
            createURLWithPort("/v1/brand/${brand.id}", port),
            HttpMethod.DELETE,
            HttpEntity(
                null,
                headers,
            ),
        )

        Thread.sleep(3000)
    }

    companion object {
        private const val RANKING_API_BASE = "/v1/ranking"
        private const val CATEGORY_WISE_LOWEST = "/category/lowest"
        private const val BRAND_WISE_LOWEST = "/brand/lowest"
        private const val CATEGORY_HIGH_LOW = "/category"

        private const val BRAND_API = "/v1/brand"
        private const val PRODUCT_API = "/v1/product"

        private val CATEGORY_WISE_LOWEST_RESULT =
            """
            {"items":[{"카테고리":"상의","브랜드":"C","가격":"10,000"},{"카테고리":"아우터","브랜드":"E","가격":"5,000"},{"카테고리":"바지","브랜드":"D","가격":"3,000"},{"카테고리":"스니커즈","브랜드":"G","가격":"9,000"},{"카테고리":"가방","브랜드":"A","가격":"2,000"},{"카테고리":"모자","브랜드":"D","가격":"1,500"},{"카테고리":"양말","브랜드":"I","가격":"1,700"},{"카테고리":"액세서리","브랜드":"F","가격":"1,900"}],"총액":"34,100"}
            """.trimIndent()
        private val BRAND_WISE_LOWEST_RESULT =
            """
            {"최저가":{"카테고리":[{"카테고리":"상의","가격":"10,100"},{"카테고리":"아우터","가격":"5,100"},{"카테고리":"바지","가격":"3,000"},{"카테고리":"스니커즈","가격":"9,500"},{"카테고리":"가방","가격":"2,500"},{"카테고리":"모자","가격":"1,500"},{"카테고리":"양말","가격":"2,400"},{"카테고리":"액세서리","가격":"2,000"}],"브랜드":"D","총액":"36,100"}}
            """.trimIndent()
        private val CATEGORY_HIGH_LOW_RESULT =
            """
            {"카테고리":"상의","최저가":{"브랜드":"C","가격":"10,000"},"최고가":{"브랜드":"I","가격":"11,400"}}
            """.trimIndent()
    }
}
