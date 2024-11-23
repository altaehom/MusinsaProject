package com.musinsa.project.domain.service.category

import com.musinsa.project.domain.entity.category.Category
import com.musinsa.project.domain.entity.category.CategoryRepository
import com.musinsa.project.domain.service.category.model.CategoryModel
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull
import kotlin.test.assertEquals
import kotlin.test.assertNull

@ExtendWith(MockKExtension::class)
class CategoryDomainServiceTest {
    @InjectMockKs
    private lateinit var mut: CategoryDomainService
    private val categoryRepository: CategoryRepository = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `save_1_저장 시에는 save가 호출 된다`() {
        val categoryName = "Category"
        val slot = slot<Category>()

        every { categoryRepository.save(capture(slot)) }.answers { slot.captured }

        mut.save(categoryName)
        verify { categoryRepository.save(slot.captured) }
    }

    @Test
    fun `get_1_값이 조회 되지 않으면 null을 반환한다`() {
        val id = 1L

        every { categoryRepository.findByIdOrNull(id) }.returns(null)

        val result = mut.get(id)
        assertNull(result)
    }

    @Test
    fun `get_2_데이터가 존재하면 도메인 모델을 반환한다`() {
        val category =
            spyk<Category>().apply {
                every { categoryName }.returns(CATEGORY_NAME)
                every { id }.returns(1L)
            }

        every { categoryRepository.findByIdOrNull(1L) }.returns(category)

        val result = mut.get(1L)
        assertEquals(result, CategoryModel(category))
    }

    companion object {
        private const val CATEGORY_NAME = "category"
    }
}
