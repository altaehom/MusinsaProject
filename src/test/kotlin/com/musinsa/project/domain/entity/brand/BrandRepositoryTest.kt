package com.musinsa.project.domain.entity.brand

import io.mockk.clearAllMocks
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest(
    properties = ["classpath:application-test.yml"],
)
@ActiveProfiles("test")
@Transactional
class BrandRepositoryTest {
    @Autowired
    private lateinit var brandRepository: BrandRepository

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun `CRD Test`() {
        val brand =
            Brand(BRAND_NAME)
                .let { brandRepository.save(it) }
        assertEquals(brand.brandName, BRAND_NAME)
        assertNull(brand.updatedAt)

        val brand2 = brandRepository.findByIdOrNull(brand.id!!)!!
        assertNotNull(brand2)
        assertEquals(brand, brand2)

        brandRepository.delete(brand2)
        assertNull(brandRepository.findByIdOrNull(brand.id!!))
    }

    companion object {
        private const val BRAND_NAME = "TestBrand"
    }
}
