package com.camphub.api.service

import com.camphub.api.dto.*
import com.camphub.api.exception.ForbiddenException
import com.camphub.api.exception.NotFoundException
import com.camphub.api.exception.UnauthorizedException
import com.camphub.api.model.ForumComment
import com.camphub.api.model.ForumThread
import com.camphub.api.model.Role
import com.camphub.api.model.User
import com.camphub.api.repository.ForumCommentRepository
import com.camphub.api.repository.ForumThreadRepository
import com.camphub.api.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ForumService(
    private val forumThreadRepository: ForumThreadRepository,
    private val forumCommentRepository: ForumCommentRepository,
    private val userRepository: UserRepository
) {
    @Transactional(readOnly = true)
    fun listThreads(): List<ForumThreadDto> {
        return forumThreadRepository.findAllByOrderByIdDesc().map { thread ->
            val count = forumCommentRepository.countByThreadId(thread.id!!).toInt()
            thread.toDto(count)
        }
    }

    @Transactional(readOnly = true)
    fun getThread(threadId: Long): ForumThreadDto {
        val thread = findThread(threadId)
        val count = forumCommentRepository.countByThreadId(threadId).toInt()
        return thread.toDto(count)
    }

    @Transactional(readOnly = true)
    fun listComments(threadId: Long): List<ForumCommentDto> {
        findThread(threadId)
        return forumCommentRepository.findByThreadIdOrderByIdAsc(threadId).map { it.toDto() }
    }

    @Transactional
    fun createThread(req: CreateForumThreadRequest, email: String): ForumThreadDto {
        val thread = ForumThread().apply {
            title = req.title.trim()
            content = req.content.trim()
            author = currentUser(email)
            createdAt = LocalDateTime.now()
        }
        forumThreadRepository.save(thread)
        return thread.toDto(0)
    }

    @Transactional
    fun updateThread(threadId: Long, req: CreateForumThreadRequest, email: String): ForumThreadDto {
        val thread = findThread(threadId)
        val user = currentUser(email)
        if (thread.author.id != user.id) {
            throw ForbiddenException("Hanya pembuat thread yang dapat mengubah thread ini")
        }
        thread.title = req.title.trim()
        thread.content = req.content.trim()
        val count = forumCommentRepository.countByThreadId(threadId).toInt()
        return thread.toDto(count)
    }

    @Transactional
    fun deleteThread(threadId: Long, email: String) {
        val thread = findThread(threadId)
        val user = currentUser(email)
        if (thread.author.id != user.id && user.role != Role.ADMIN) {
            throw ForbiddenException("Hanya pembuat thread atau admin yang dapat menghapus thread ini")
        }
        val comments = forumCommentRepository.findByThreadIdOrderByIdAsc(threadId)
        forumCommentRepository.deleteAll(comments)
        forumThreadRepository.delete(thread)
    }

    @Transactional
    fun addComment(threadId: Long, req: CreateForumCommentRequest, email: String): ForumCommentDto {
        val thread = findThread(threadId)
        val comment = ForumComment().apply {
            this.thread = thread
            this.author = currentUser(email)
            content = req.content.trim()
            createdAt = LocalDateTime.now()
        }
        forumCommentRepository.save(comment)
        return comment.toDto()
    }

    @Transactional
    fun deleteComment(commentId: Long, email: String) {
        val comment = forumCommentRepository.findById(commentId)
            .orElseThrow { NotFoundException("Komentar id=$commentId tidak ditemukan") }
        val user = currentUser(email)
        if (comment.author.id != user.id && user.role != Role.ADMIN) {
            throw ForbiddenException("Anda tidak memiliki izin untuk menghapus komentar ini")
        }
        forumCommentRepository.delete(comment)
    }

    private fun findThread(id: Long): ForumThread =
        forumThreadRepository.findById(id).orElseThrow { NotFoundException("Thread id=$id tidak ditemukan") }

    private fun currentUser(email: String): User =
        userRepository.findByEmail(email) ?: throw UnauthorizedException()
}
