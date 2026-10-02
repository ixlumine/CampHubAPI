package com.camphub.api.dto

import com.camphub.api.model.ForumComment
import com.camphub.api.model.ForumPost
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

// Request DTO for creating/updating a post
data class CreateForumPostRequest(
    @field:NotBlank(message = "Judul forum wajib diisi")
    @field:Size(max = 200, message = "Judul maksimal 200 karakter")
    val title: String,

    @field:NotBlank(message = "Konten forum wajib diisi")
    @field:Size(max = 10000, message = "Konten maksimal 10000 karakter")
    val content: String
)

// Request DTO for creating a comment
data class CreateForumCommentRequest(
    @field:NotBlank(message = "Komentar wajib diisi")
    @field:Size(max = 5000, message = "Komentar maksimal 5000 karakter")
    val content: String
)

// Response DTO for comment
data class ForumCommentDto(
    val id: Long?,
    val postId: Long?,
    val userId: Long?,
    val userName: String,
    val content: String
)

// Response DTO for post
data class ForumPostDto(
    val id: Long?,
    val title: String,
    val content: String,
    val userId: Long?,
    val userName: String,
    val commentsCount: Int
)

// Mapper comment → DTO
fun ForumComment.toDto() =
    ForumCommentDto(
        id = this.id,
        postId = this.post.id,
        userId = this.user.id,
        userName = this.user.name,
        content = this.content
    )

// Mapper post → DTO
fun ForumPost.toDto(commentsCount: Int = 0) =
    ForumPostDto(
        id = this.id,
        title = this.title,
        content = this.content,
        userId = this.user.id,
        userName = this.user.name,
        commentsCount = commentsCount
    )
