package com.musinsa.project.webapi.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/board")
class BoardController {
    @GetMapping
    fun test() = Test("aaaaa")

    data class Test(
        val name: String,
    )
}
