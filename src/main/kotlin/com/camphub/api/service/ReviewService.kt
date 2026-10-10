package com.camphub.api.service

import com.camphub.api.dto.*
import com.camphub.api.exception.BadRequestException
import com.camphub.api.exception.ForbiddenException
import com.camphub.api.exception.NotFoundException
import com.camphub.api.exception.UnauthorizedException
import com.camphub.api.model.Bootcamp
import com.camphub.api.model.CareerStatus
import com.camphub.api.model.Review
import com.camphub.api.model.Role
import com.camphub.api.model.User
import com.camphub.api.repository.BootcampRepository
import com.camphub.api.repository.ReviewRepository
import com.camphub.api.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import kotlin.math.round

@Service
class ReviewService(
    private val reviewRepository: ReviewRepository,
    private val bootcampRepository: BootcampRepository,
    private val userRepository: UserRepository
) {
    private val log = LoggerFactory.getLogger(ReviewService::class.java)

    @Transactional(readOnly = true)
    fun listByBootcamp(bootcampId: Long): List<ReviewDto> {
        if (!bootcampRepository.existsById(bootcampId)) {
            throw NotFoundException("Bootcamp id=$bootcampId tidak ditemukan")
        }
        return reviewRepository.findByBootcampIdOrderByCreatedAtDesc(bootcampId).map { it.toDto() }
    }

    @Transactional
    fun create(bootcampId: Long, req: CreateReviewRequest, email: String): ReviewDto {
        val bootcamp = bootcampRepository.findById(bootcampId)
            .orElseThrow { NotFoundException("Bootcamp id=$bootcampId tidak ditemukan") }
        val user = currentUser(email)

        if (reviewRepository.existsByBootcampIdAndAuthorId(bootcamp.id!!, user.id!!)) {
            throw BadRequestException("Anda sudah memberikan ulasan untuk bootcamp ini")
        }

        val review = Review().apply {
            this.bootcamp = bootcamp
            this.author = user
            this.rating = req.rating!!
            this.content = req.content.trim()
            this.careerStatus = req.careerStatus!!
            this.createdAt = LocalDateTime.now()
        }

        reviewRepository.save(review)
        log.info("Review id={} created for bootcamp id={} by user id={}", review.id, bootcamp.id, user.id)
        return review.toDto()
    }

    @Transactional
    fun update(id: Long, req: CreateReviewRequest, email: String): ReviewDto {
        val review = findReview(id)
        val user = currentUser(email)

        if (review.author.id != user.id) {
            throw ForbiddenException("Hanya pemilik yang dapat mengubah ulasan ini")
        }

        review.apply {
            this.rating = req.rating!!
            this.content = req.content.trim()
            this.careerStatus = req.careerStatus!!
        }
        log.info("Review id={} updated by user id={}", review.id, user.id)
        return review.toDto()
    }

    @Transactional
    fun delete(id: Long, email: String) {
        val review = findReview(id)
        val user = currentUser(email)

        if (review.author.id != user.id && user.role != Role.ADMIN) {
            throw ForbiddenException("Hanya pemilik atau admin yang dapat menghapus ulasan ini")
        }

        reviewRepository.delete(review)
        log.info("Review id={} deleted by user id={}", id, user.id)
    }

    @Transactional(readOnly = true)
    fun getSummary(bootcampId: Long): ReviewSummaryDto {
        val bootcamp = bootcampRepository.findById(bootcampId)
            .orElseThrow { NotFoundException("Bootcamp id=$bootcampId tidak ditemukan") }

        val reviews = reviewRepository.findByBootcampId(bootcamp.id!!)
        if (reviews.isEmpty()) {
            return ReviewSummaryDto(
                bootcampId = bootcamp.id,
                averageRating = 0.0,
                reviewCount = 0L,
                employedCount = 0L,
                seekingJobCount = 0L
            )
        }

        val rawAvg = reviews.map { it.rating }.average()
        val avg = round(rawAvg * 10.0) / 10.0
        val employed = reviews.count { it.careerStatus == CareerStatus.EMPLOYED }.toLong()
        val seeking = reviews.count { it.careerStatus == CareerStatus.SEEKING_JOB }.toLong()

        return ReviewSummaryDto(
            bootcampId = bootcamp.id,
            averageRating = avg,
            reviewCount = reviews.size.toLong(),
            employedCount = employed,
            seekingJobCount = seeking
        )
    }

    @Transactional(readOnly = true)
    fun getRankings(): List<RankingDto> {
        val bootcamps = bootcampRepository.findAll()
        val allReviews = reviewRepository.findAll()
        val reviewsByBootcamp = allReviews.groupBy { it.bootcamp.id }

        data class Candidate(val bootcamp: Bootcamp, val avg: Double, val count: Long)

        val candidates = bootcamps.mapNotNull { bootcamp ->
            val reviews = reviewsByBootcamp[bootcamp.id] ?: emptyList()
            if (reviews.size < 3) return@mapNotNull null
            val rawAvg = reviews.map { it.rating }.average()
            val avg = round(rawAvg * 10.0) / 10.0
            Candidate(bootcamp, avg, reviews.size.toLong())
        }.sortedWith(
            compareByDescending<Candidate> { it.avg }
                .thenByDescending { it.count }
        )

        return candidates.mapIndexed { index, candidate ->
            RankingDto(
                rank = index + 1,
                bootcampId = candidate.bootcamp.id,
                bootcampName = candidate.bootcamp.name,
                location = candidate.bootcamp.location,
                averageRating = candidate.avg,
                reviewCount = candidate.count
            )
        }
    }

    private fun findReview(id: Long): Review =
        reviewRepository.findById(id).orElseThrow { NotFoundException("Ulasan id=$id tidak ditemukan") }

    private fun currentUser(email: String): User =
        userRepository.findByEmail(email) ?: throw UnauthorizedException()
}