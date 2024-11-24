package com.musinsa.project.application.product

import com.musinsa.project.application.exception.ApplicationException.BrandNotFoundException
import com.musinsa.project.application.exception.ApplicationException.CategoryNotFoundException
import com.musinsa.project.application.exception.ApplicationException.ProductNotFoundException
import com.musinsa.project.application.exception.ApplicationException.ProductPriceException
import com.musinsa.project.domain.entity.brand.Brand
import com.musinsa.project.domain.entity.category.Category
import com.musinsa.project.domain.entity.product.Product
import com.musinsa.project.domain.service.brand.BrandDomainService
import com.musinsa.project.domain.service.brand.model.BrandModel
import com.musinsa.project.domain.service.category.CategoryDomainService
import com.musinsa.project.domain.service.category.model.CategoryModel
import com.musinsa.project.domain.service.product.ProductDomainService
import com.musinsa.project.domain.service.product.model.ProductModel
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.time.Instant
import kotlin.test.assertFailsWith

@ExtendWith(MockKExtension::class)
class ProductAdminServiceTest {
    @InjectMockKs
    private lateinit var mut: ProductAdminService
    private val brandDomainService: BrandDomainService = mockk(relaxed = true)
    private val categoryDomainService: CategoryDomainService = mockk(relaxed = true)
    private val productDomainService: ProductDomainService = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `createProduct_1_금액이 음수면 에러를 반환한다`() {
        val brandId = 1L
        val categoryId = 1L
        val price = BigDecimal.ONE.negate()

        assertFailsWith<ProductPriceException> {
            mut.createProduct(
                brandId = brandId,
                categoryId = categoryId,
                price = price,
            )
        }
        verify(exactly = 0) { productDomainService.save(any(), any(), any()) }
    }

    @Test
    fun `createProduct_2_카테고리를 찾을 수 없으면 에러를 반환한다`() {
        val brandId = 1L
        val categoryId = 1L
        val price = BigDecimal.ONE

        every { categoryDomainService.get(categoryId) }.returns(null)

        assertFailsWith<CategoryNotFoundException> {
            mut.createProduct(
                brandId = brandId,
                categoryId = categoryId,
                price = price,
            )
        }
        verify(exactly = 0) { productDomainService.save(any(), any(), any()) }
    }

    @Test
    fun `createProduct_3_브랜드를 찾을 수 없으면 에러를 반환한다`() {
        val brandId = 1L
        val categoryId = 1L
        val price = BigDecimal.ONE

        every { categoryDomainService.get(categoryId) }.returns(CategoryModel(spyk<Category>().apply { every { id }.returns(categoryId) }))
        every { brandDomainService.get(categoryId) }.returns(null)

        assertFailsWith<BrandNotFoundException> {
            mut.createProduct(
                brandId = brandId,
                categoryId = categoryId,
                price = price,
            )
        }
        verify(exactly = 0) { productDomainService.save(any(), any(), any()) }
    }

    @Test
    fun `createProduct_4_저장 시에는 save 메소드를 호출한다`() {
        val brandId = 1L
        val categoryId = 1L
        val price = BigDecimal.ONE

        every { categoryDomainService.get(categoryId) }.returns(CategoryModel(spyk<Category>().apply { every { id }.returns(categoryId) }))
        every { brandDomainService.get(categoryId) }.returns(BrandModel(spyk<Brand>().apply { every { id }.returns(brandId) }))

        mut.createProduct(
            brandId = brandId,
            categoryId = categoryId,
            price = price,
        )

        verify {
            productDomainService.save(
                price = price,
                brandId = brandId,
                categoryId = categoryId,
            )
        }
    }

    @Test
    fun `updateProduct_1_가격이 음수면 에러를 반환한다`() {
        val id = 1L
        val price = BigDecimal.ONE.negate()

        assertFailsWith<ProductPriceException> {
            mut.updateProduct(
                id = id,
                price = price,
            )
        }
        verify(exactly = 0) { productDomainService.update(any(), any()) }
    }

    @Test
    fun `updateProduct_2_기등록 된 상품을 찾지 못하면 에러를 반환한다`() {
        val id = 1L
        val price = BigDecimal.ONE

        every { productDomainService.get(id) }.returns(null)

        assertFailsWith<ProductNotFoundException> {
            mut.updateProduct(
                id = id,
                price = price,
            )
        }
        verify(exactly = 0) { productDomainService.update(any(), any()) }
    }

    @Test
    fun `updateProduct_3_수정 시에는 update 메소드가 호출 된다`() {
        val productId = 1L
        val price = BigDecimal.ONE

        every { productDomainService.get(productId) }.returns(
            ProductModel(
                id = productId,
                brandId = 2L,
                categoryId = 3L,
                price = BigDecimal.TEN,
                Instant.now(),null
            )
        )

        mut.updateProduct(
            id = productId,
            price = price,
        )
        verify {
            productDomainService.update(
                id = productId,
                price = price,
            )
        }
    }

    @Test
    fun `removeProduct_1_기 등록 된 상품을 찾지 못하면 에러를 반환한다`() {
        val id = 1L

        every { productDomainService.get(id) }.returns(null)

        assertFailsWith<ProductNotFoundException> {
            mut.removeProduct(id)
        }
        verify(exactly = 0) { productDomainService.delete(id) }
    }

    @Test
    fun `removeProduct_2_삭제 시에는 delete 메소드가 호출 된다`() {
        val productId = 1L

        every { productDomainService.get(productId) }.returns(
            ProductModel(
                id = productId,
                brandId = 2L,
                categoryId = 3L,
                price = BigDecimal.TEN,
                Instant.now(),null
            )
        )

        mut.removeProduct(productId)
        verify { productDomainService.delete(productId) }
    }
}
