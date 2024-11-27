package com.musinsa.project.webapi.api.utils

internal object ItTestUtils {
    internal fun createURLWithPort(
        uri: String,
        port: Int,
    ): String = "http://localhost:$port$uri"
}
