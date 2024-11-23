package com.musinsa.project.application.brand

import com.musinsa.project.application.exception.ApplicationException.CommonException
import com.musinsa.project.application.exception.ApplicationException.NotFoundException
import com.musinsa.project.domain.entity.brand.Brand
import com.musinsa.project.domain.service.brand.BrandDomainService
import com.musinsa.project.domain.service.brand.model.BrandModel
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
import kotlin.test.assertFailsWith

@ExtendWith(MockKExtension::class)
class BrandAdminServiceTest {
    @InjectMockKs
    private lateinit var mut: BrandAdminService
    private val brandDomainService: BrandDomainService = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @Test
    fun `createBrand_1_파라미터가 공백이면 에러를 반환한다`() {
        assertFailsWith<CommonException> {
            mut.createBrand("")
        }

        verify(exactly = 0) { brandDomainService.save(any()) }
    }

    @Test
    fun `createBrand_2_정상 호출 된다`() {
        val brandName = "TestBrand"
        mut.createBrand(brandName)

        verify { brandDomainService.save(brandName) }
    }

    @Test
    fun `updateBrand_1_파라미터가 공백이면 에러를 반환한다`() {
        assertFailsWith<CommonException> {
            mut.updateBrand(1L, "")
        }

        verify(exactly = 0) { brandDomainService.update(any(), any()) }
    }

    @Test
    fun `updateBrand_2_엔티티가 조회 되지 않으면 에러를 반환한다`() {
        val id = 1L
        val brandName = "TestBrand"

        every { brandDomainService.get(id) }.returns(null)

        assertFailsWith<NotFoundException> {
            mut.updateBrand(id, brandName)
        }

        verify(exactly = 0) { brandDomainService.update(any(), any()) }
    }

    @Test
    fun `updateBrand_3_정상 호출 된다`() {
        val brandId = 1L
        val brandName = "TestBrand"

        every { brandDomainService.get(brandId) }.returns(
            BrandModel(spyk<Brand>().apply { every { id }.returns(brandId) }),
        )

        mut.updateBrand(brandId, brandName)

        verify { brandDomainService.update(brandId, brandName) }
    }

    @Test
    fun `removeBrand_1_엔티티가 조회 되지 않으면 에러를 반환한다`() {
        val id = 1L

        every { brandDomainService.get(id) }.returns(null)

        assertFailsWith<NotFoundException> {
            mut.removeBrand(id)
        }

        verify(exactly = 0) { brandDomainService.delete(any()) }
    }

    @Test
    fun `removeBrand_2_정상 호출 된다`() {
        val brandId = 1L

        every { brandDomainService.get(brandId) }.returns(
            BrandModel(spyk<Brand>().apply { every { id }.returns(brandId) }),
        )

        mut.removeBrand(brandId)

        verify { brandDomainService.delete(brandId) }
    }
}
