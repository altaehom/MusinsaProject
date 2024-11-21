package com.musinsa.project.domain.exception

sealed class DomainException(
    private val msg: String,
) : IllegalStateException(msg) {
    class NotFoundException(
        msg: String,
    ) : DomainException(msg)
}
