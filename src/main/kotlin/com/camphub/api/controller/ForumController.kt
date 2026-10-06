package com.camphub.api.controller

import com.camphub.api.dto.*
import com.camphub.api.service.ForumService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User as SpringUser
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class ForumController(private val service: ForumService) {

    @GetMapping("/threads")
    fun listThreads(): List<ForumThreadDto> = service.listThreads()

    @GetMapping("/threads/{id}")
    fun getThread(@PathVariable id: Long): ForumThreadDto = service.getThread(id)

    @PostMapping("/threads")
    @ResponseStatus(HttpStatus.CREATED)
    fun createThread(
        @Valid @RequestBody req: CreateForumThreadRequest,
        @AuthenticationPrincipal principal: SpringUser
    ): ForumThreadDto = service.createThread(req, principal.username)

    @PutMapping("/threads/{id}")
    fun updateThread(
        @PathVariable id: Long,
        @Valid @RequestBody req: CreateForumThreadRequest,
        @AuthenticationPrincipal principal: SpringUser
    ): ForumThreadDto = service.updateThread(id, req, principal.username)

    @DeleteMapping("/threads/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteThread(
        @PathVariable id: Long,
        @AuthenticationPrincipal principal: SpringUser
    ) = service.deleteThread(id, principal.username)

    @GetMapping("/threads/{id}/comments")
    fun listComments(@PathVariable id: Long): List<ForumCommentDto> = service.listComments(id)

    @PostMapping("/threads/{threadId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    fun addComment(
        @PathVariable threadId: Long,
        @Valid @RequestBody req: CreateForumCommentRequest,
        @AuthenticationPrincipal principal: SpringUser
    ): ForumCommentDto = service.addComment(threadId, req, principal.username)

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteComment(
        @PathVariable commentId: Long,
        @AuthenticationPrincipal principal: SpringUser
    ) = service.deleteComment(commentId, principal.username)
}
