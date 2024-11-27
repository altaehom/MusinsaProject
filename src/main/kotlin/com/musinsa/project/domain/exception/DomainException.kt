package com.musinsa.project.domain.exception

/**
 * 도메인 레이어에서 발생하는 익셉션
 */
sealed class DomainException(
    private val msg: String,
) : IllegalStateException(msg) {
    class DomainNotFoundException(
        id: Long,
    ) : DomainException("Not Found Entity.. [id: $id]")
}
