package com.musinsa.project.domain.entity.board

import org.springframework.data.jpa.repository.JpaRepository

interface BoardRepository : JpaRepository<Board, Long>
