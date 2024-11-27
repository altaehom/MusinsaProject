package com.musinsa.project.domain.service.category

import com.musinsa.project.domain.entity.category.Category
import com.musinsa.project.domain.entity.category.CategoryRepository
import com.musinsa.project.domain.service.category.model.CategoryModel
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CategoryDomainService(
    private val categoryRepository: CategoryRepository,
) {
    @Transactional
    fun save(categoryName: String) {
        categoryRepository.save(Category(categoryName))
    }

    fun get(id: Long) = categoryRepository.findByIdOrNull(id)?.let(CategoryModel::invoke)

    fun getAll() = categoryRepository.findAll().map(CategoryModel::invoke)

    @Profile("test")
    @Transactional
    fun clear() = categoryRepository.clear()

    fun getByCategoryName(categoryName: String) = categoryRepository.findByCategoryName(categoryName).first().let(CategoryModel::invoke)

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}
