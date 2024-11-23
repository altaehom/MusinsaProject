package com.musinsa.project.domain.entity.category

import io.mockk.clearAllMocks
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
class CategoryRepositoryTest {
    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `CRD Test`() {
        val category =
            Category(CATEGORY_NAME)
                .let { categoryRepository.save(it) }
        assertEquals(category.categoryName, CATEGORY_NAME)
        assertNull(category.updatedAt)

        val category2 = categoryRepository.findByIdOrNull(category.id!!)
        assertNotNull(category2)
        assertEquals(category, category2)

        categoryRepository.deleteById(category.id!!)
        assertNull(categoryRepository.findByIdOrNull(category.id!!))
    }

    companion object {
        private const val CATEGORY_NAME = "TestBrandCategory"
    }
}
