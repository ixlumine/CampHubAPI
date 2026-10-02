package com.camphub.api.controller

import com.camphub.api.dto.*
import com.camphub.api.service.ForumService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User as SpringUser
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/forum")
class ForumController(private val service: ForumService) {

    @GetMapping("/posts")
    fun listPosts(): List<ForumPostDto> = service.listPosts()

    @GetMapping("/posts/{id}")
    fun getPostDetail(@PathVariable id: Long): Map<String, Any> {
        val (post, comments) = service.getPostDetail(id)
        return mapOf("post" to post, "comments" to comments)
    }

    @PostMapping("/posts")
    @ResponseStatus(HttpStatus.CREATED)
    fun createPost(
        @Valid @RequestBody req: CreateForumPostRequest,
        @AuthenticationPrincipal principal: SpringUser
    ): ForumPostDto = service.createPost(req, principal.username)

    @PutMapping("/posts/{id}")
    fun updatePost(
        @PathVariable id: Long,
        @Valid @RequestBody req: CreateForumPostRequest,
        @AuthenticationPrincipal principal: SpringUser
    ): ForumPostDto = service.updatePost(id, req, principal.username)

    @DeleteMapping("/posts/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletePost(
        @PathVariable id: Long,
        @AuthenticationPrincipal principal: SpringUser
    ) = service.deletePost(id, principal.username)

    @PostMapping("/posts/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    fun addComment(
        @PathVariable postId: Long,
        @Valid @RequestBody req: CreateForumCommentRequest,
        @AuthenticationPrincipal principal: SpringUser
    ): ForumCommentDto = service.addComment(postId, req, principal.username)

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteComment(
        @PathVariable commentId: Long,
        @AuthenticationPrincipal principal: SpringUser
    ) = service.deleteComment(commentId, principal.username)
}
