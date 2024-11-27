package com.musinsa.project.application.exception

/**
 * 비즈니스 로직에서 발생하는 익셉션
 */
sealed class ApplicationException(
    private val msg: String,
) : RuntimeException(msg) {
    class BrandNotFoundException : ApplicationException("Not Found!")

    class CategoryNotFoundException : ApplicationException("Not Found!")

    class ProductNotFoundException : ApplicationException("Not Found!")

    class ProductPriceException : ApplicationException("Price is a negative quantity")

    class RankingNotFoundException : ApplicationException("Not Found Rank!")

    class CommonException(
        msg: String,
    ) : ApplicationException(msg)
}
