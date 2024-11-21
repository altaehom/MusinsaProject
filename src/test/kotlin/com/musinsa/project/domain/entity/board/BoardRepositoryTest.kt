package com.musinsa.project.domain.entity.board

import io.mockk.clearAllMocks
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional(readOnly = true)
class BoardRepositoryTest {
    @Autowired
    private lateinit var boardRepository: BoardRepository

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    @Transactional
    fun www() {
        val board =
            Board(
                title = "11",
                body = "111",
            )
        val result = boardRepository.save(board)

        assertEquals(board.title, result.title)
    }
}
