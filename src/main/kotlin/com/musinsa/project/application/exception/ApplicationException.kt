package com.musinsa.project.application.exception

sealed class ApplicationException(
    private val msg: String,
) : RuntimeException(msg) {
    class NotFoundException(
        msg: String,
    ) : ApplicationException(msg)
}
