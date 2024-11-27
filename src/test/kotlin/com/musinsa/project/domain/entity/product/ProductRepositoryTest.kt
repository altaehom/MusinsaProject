package com.musinsa.project.domain.entity.product

import io.mockk.clearAllMocks
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest(
    properties = ["classpath:application-test.yml"],
)
@Transactional
class ProductRepositoryTest {
    @Autowired
    private lateinit var productRepository: ProductRepository

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `CRUD Test`() {
        val brandId = 1L
        val categoryId = 1L
        val product =
            Product(brandId, categoryId, price = ZERO)
                .let { productRepository.save(it) }
        assertEquals(product.price, ZERO)
        assertNull(product.updatedAt)

        val product2 = productRepository.findByIdOrNull(product.id!!)
        assertNotNull(product2)
        assertEquals(product, product2)

        productRepository.delete(product2)
        assertNull(productRepository.findByIdOrNull(product.id!!))
    }

    @Test
    fun `findByBrandId Test`() {
        val brandId = 99L
        val categoryId = 1L
        val product =
            Product(brandId, categoryId, price = ZERO)
                .let { productRepository.save(it) }

        val product2 = productRepository.findByBrandId(brandId)
        assertNotNull(product2)
        assertEquals(listOf(product), product2)
    }

    companion object {
        private val ZERO = BigDecimal.ZERO.setScale(2)
    }
}
