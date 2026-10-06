package com.camphub.api.dto

import com.camphub.api.model.ForumComment
import com.camphub.api.model.ForumThread
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class CreateForumThreadRequest(
    @field:NotBlank(message = "Judul forum wajib diisi")
    @field:Size(max = 200, message = "Judul maksimal 200 karakter")
    val title: String,

    @field:NotBlank(message = "Konten forum wajib diisi")
    @field:Size(max = 5000, message = "Konten maksimal 5000 karakter")
    val content: String
)

data class CreateForumCommentRequest(
    @field:NotBlank(message = "Komentar wajib diisi")
    @field:Size(max = 5000, message = "Komentar maksimal 5000 karakter")
    val content: String
)

data class ForumThreadDto(
    val id: Long?,
    val authorId: Long?,
    val authorName: String,
    val title: String,
    val content: String,
    val commentCount: Int,
    val createdAt: LocalDateTime
)

data class ForumCommentDto(
    val id: Long?,
    val threadId: Long?,
    val authorId: Long?,
    val authorName: String,
    val content: String,
    val createdAt: LocalDateTime
)

fun ForumThread.toDto(commentCount: Int = 0) = ForumThreadDto(
    id = this.id,
    authorId = this.author.id,
    authorName = this.author.name,
    title = this.title,
    content = this.content,
    commentCount = commentCount,
    createdAt = this.createdAt
)

fun ForumComment.toDto() = ForumCommentDto(
    id = this.id,
    threadId = this.thread.id,
    authorId = this.author.id,
    authorName = this.author.name,
    content = this.content,
    createdAt = this.createdAt
)
