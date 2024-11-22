package com.musinsa.project.webapi.api

import com.musinsa.project.MusinsaProjectApplication
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.configurationprocessor.json.JSONException
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(
    classes = [MusinsaProjectApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
class BoardControllerIntegrationTest {
    @LocalServerPort
    private val port = 0

    val restTemplate: TestRestTemplate = TestRestTemplate()

    val headers: HttpHeaders = HttpHeaders()

    @Test
    @Throws(JSONException::class)
    fun testRetrieveStudentCourse() {
        val entity: HttpEntity<String> = HttpEntity(null, headers)

        val response =
            restTemplate.exchange(
                createURLWithPort("/board"),
                HttpMethod.GET,
                entity,
                String::class.java,
            )

        val expected =
            """
            {"name":"aaaaa"}
            """.trimIndent()

        assertEquals(expected, response.body)
    }

    private fun createURLWithPort(uri: String): String = "http://localhost:$port$uri"
}
