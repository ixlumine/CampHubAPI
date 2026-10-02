package com.camphub.api.repository

import com.camphub.api.model.ForumPost
import org.springframework.data.jpa.repository.JpaRepository

interface ForumPostRepository : JpaRepository<ForumPost, Long> {
    fun findAllByOrderByIdDesc(): List<ForumPost>
}
