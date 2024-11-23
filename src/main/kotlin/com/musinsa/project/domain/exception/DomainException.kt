package com.musinsa.project.domain.exception

sealed class DomainException(
    private val msg: String,
) : IllegalStateException(msg) {
    class DomainNotFoundException(
        id: Long,
    ) : DomainException("Not Found Entity.. [id: $id]")
}
