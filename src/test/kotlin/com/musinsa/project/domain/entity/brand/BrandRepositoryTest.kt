package com.musinsa.project.domain.entity.brand

import io.mockk.clearAllMocks
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(
    properties = arrayOf("classpath:application-test.yml"),
)
@Transactional(readOnly = true)
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
    @Transactional
    fun `CRUD Test`() {
        val brand = Brand().let { brandRepository.save(it) }

        println(brand)
    }
}
