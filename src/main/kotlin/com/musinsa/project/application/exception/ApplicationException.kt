package com.musinsa.project.application.exception

sealed class ApplicationException(
    private val msg: String,
) : RuntimeException(msg) {
    class NotFoundException : ApplicationException("Not Found!")

    class CommonException(
        msg: String,
    ) : ApplicationException(msg)
}
