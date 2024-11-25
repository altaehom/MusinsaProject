package com.musinsa.project.domain.service.category

import com.musinsa.project.domain.entity.category.Category
import com.musinsa.project.domain.entity.category.CategoryRepository
import com.musinsa.project.domain.service.category.model.CategoryModel
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CategoryDomainService(
    private val categoryRepository: CategoryRepository,
) {
    private lateinit var categoryIdMap: Map<Long, CategoryModel>
    private lateinit var categoryNameMap: Map<String, CategoryModel>

    @PostConstruct
    fun postInit() {
        val categories = categoryRepository.findAll()
        categoryIdMap =
            categories
                .map(CategoryModel::invoke)
                .associateBy { it.id }
        categoryNameMap =
            categories
                .map(CategoryModel::invoke)
                .associateBy { it.categoryName }
    }

    @Transactional
    fun save(categoryName: String) {
        categoryRepository.save(Category(categoryName))
    }

    fun get(id: Long) = categoryIdMap[id]

    fun getAll() = categoryIdMap.values

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}
