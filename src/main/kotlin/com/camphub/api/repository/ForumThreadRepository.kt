package com.camphub.api.repository

import com.camphub.api.model.ForumThread
import org.springframework.data.jpa.repository.JpaRepository

interface ForumThreadRepository : JpaRepository<ForumThread, Long> {
    fun findAllByOrderByIdDesc(): List<ForumThread>
}
