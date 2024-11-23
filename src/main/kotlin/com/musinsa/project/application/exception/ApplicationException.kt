package com.musinsa.project.application.exception

sealed class ApplicationException(
    private val msg: String,
) : RuntimeException(msg) {
    class BrandNotFoundException : ApplicationException("Not Found!")

    class CategoryNotFoundException : ApplicationException("Not Found!")

    class ProductNotFoundException : ApplicationException("Not Found!")

    class ProductPriceException : ApplicationException("Price is a negative quantity")

    class CommonException(
        msg: String,
    ) : ApplicationException(msg)
}
