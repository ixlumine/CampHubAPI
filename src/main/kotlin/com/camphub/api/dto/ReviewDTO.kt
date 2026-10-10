package com.camphub.api.dto

import com.camphub.api.model.CareerStatus
import com.camphub.api.model.Review
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

// Request
data class CreateReviewRequest(
    @field:NotNull(message = "Rating wajib diisi")
    @field:Min(value = 1, message = "Rating minimal 1")
    @field:Max(value = 5, message = "Rating maksimal 5")
    val rating: Int?,

    @field:NotBlank(message = "Konten ulasan tidak boleh kosong")
    @field:Size(max = 5000, message = "Konten ulasan maksimal 5000 karakter")
    val content: String,

    @field:NotNull(message = "Status karier wajib diisi")
    val careerStatus: CareerStatus?
)

// Response Detail
data class ReviewDto(
    val id: Long?,
    val bootcampId: Long?,
    val bootcampName: String,
    val authorId: Long?,
    val authorName: String,
    val rating: Int,
    val content: String,
    val careerStatus: CareerStatus,
    val createdAt: LocalDateTime
)

// Response Ringkasan
data class ReviewSummaryDto(
    val bootcampId: Long?,
    val averageRating: Double,
    val reviewCount: Long,
    val employedCount: Long,
    val seekingJobCount: Long
)

// Response Peringkat
data class RankingDto(
    val rank: Int,
    val bootcampId: Long?,
    val bootcampName: String,
    val location: String,
    val averageRating: Double,
    val reviewCount: Long
)

// Converter extension function
fun Review.toDto() = ReviewDto(
    id = this.id,
    bootcampId = this.bootcamp.id,
    bootcampName = this.bootcamp.name,
    authorId = this.author.id,
    authorName = this.author.name,
    rating = this.rating,
    content = this.content,
    careerStatus = this.careerStatus,
    createdAt = this.createdAt
)