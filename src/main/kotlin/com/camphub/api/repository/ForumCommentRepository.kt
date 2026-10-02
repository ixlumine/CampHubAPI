package com.camphub.api.repository

import com.camphub.api.model.ForumComment
import org.springframework.data.jpa.repository.JpaRepository

interface ForumCommentRepository : JpaRepository<ForumComment, Long> {
    fun findByPostIdOrderByIdAsc(postId: Long): List<ForumComment>
}
