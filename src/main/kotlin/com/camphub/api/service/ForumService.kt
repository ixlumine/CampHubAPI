package com.camphub.api.service

import com.camphub.api.dto.*
import com.camphub.api.exception.ForbiddenException
import com.camphub.api.exception.NotFoundException
import com.camphub.api.exception.UnauthorizedException
import com.camphub.api.model.ForumComment
import com.camphub.api.model.ForumPost
import com.camphub.api.model.Role
import com.camphub.api.model.User
import com.camphub.api.repository.ForumCommentRepository
import com.camphub.api.repository.ForumPostRepository
import com.camphub.api.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ForumService(
    private val forumPostRepository: ForumPostRepository,
    private val forumCommentRepository: ForumCommentRepository,
    private val userRepository: UserRepository
) {
    @Transactional(readOnly = true)
    fun listPosts(): List<ForumPostDto> {
        return forumPostRepository.findAllByOrderByIdDesc().map { post ->
            val count = forumCommentRepository.findByPostIdOrderByIdAsc(post.id!!).size
            post.toDto(count)
        }
    }

    @Transactional(readOnly = true)
    fun getPostDetail(postId: Long): Pair<ForumPostDto, List<ForumCommentDto>> {
        val post = findPost(postId)
        val comments = forumCommentRepository.findByPostIdOrderByIdAsc(postId).map { it.toDto() }
        return Pair(post.toDto(comments.size), comments)
    }

    @Transactional
    fun createPost(req: CreateForumPostRequest, email: String): ForumPostDto {
        val post = ForumPost().apply {
            title = req.title.trim()
            content = req.content.trim()
            user = currentUser(email)
        }
        forumPostRepository.save(post)
        return post.toDto(0)
    }

    @Transactional
    fun updatePost(postId: Long, req: CreateForumPostRequest, email: String): ForumPostDto {
        val post = findPost(postId)
        val user = currentUser(email)
        if (post.user.id != user.id && user.role != Role.ADMIN) {
            throw ForbiddenException("Hanya pembuat diskusi atau admin yang dapat mengubah postingan ini")
        }
        post.title = req.title.trim()
        post.content = req.content.trim()
        val count = forumCommentRepository.findByPostIdOrderByIdAsc(postId).size
        return post.toDto(count)
    }

    @Transactional
    fun deletePost(postId: Long, email: String) {
        val post = findPost(postId)
        val user = currentUser(email)
        if (post.user.id != user.id && user.role != Role.ADMIN) {
            throw ForbiddenException("Hanya pembuat diskusi atau admin yang dapat menghapus postingan ini")
        }
        // Delete associated comments first or let cascade handle it if configured, but safe to delete manually:
        val comments = forumCommentRepository.findByPostIdOrderByIdAsc(postId)
        forumCommentRepository.deleteAll(comments)
        forumPostRepository.delete(post)
    }

    @Transactional
    fun addComment(postId: Long, req: CreateForumCommentRequest, email: String): ForumCommentDto {
        val post = findPost(postId)
        val comment = ForumComment().apply {
            this.post = post
            this.user = currentUser(email)
            content = req.content.trim()
        }
        forumCommentRepository.save(comment)
        return comment.toDto()
    }

    @Transactional
    fun deleteComment(commentId: Long, email: String) {
        val comment = forumCommentRepository.findById(commentId)
            .orElseThrow { NotFoundException("Komentar id=$commentId tidak ditemukan") }
        val user = currentUser(email)
        // Author of comment, author of post, or admin can delete
        if (comment.user.id != user.id && comment.post.user.id != user.id && user.role != Role.ADMIN) {
            throw ForbiddenException("Anda tidak memiliki izin untuk menghapus komentar ini")
        }
        forumCommentRepository.delete(comment)
    }

    private fun findPost(id: Long): ForumPost =
        forumPostRepository.findById(id).orElseThrow { NotFoundException("Diskusi forum id=$id tidak ditemukan") }

    private fun currentUser(email: String): User =
        userRepository.findByEmail(email) ?: throw UnauthorizedException()
}
