package com.camphub.api.controller

import com.camphub.api.dto.*
import com.camphub.api.service.ReviewService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User as SpringUser
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class ReviewController(private val reviewService: ReviewService) {

    @GetMapping("/bootcamps/{id}/reviews")
    fun listByBootcamp(@PathVariable id: Long): List<ReviewDto> =
        reviewService.listByBootcamp(id)

    @PostMapping("/bootcamps/{id}/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @PathVariable id: Long,
        @Valid @RequestBody req: CreateReviewRequest,
        @AuthenticationPrincipal principal: SpringUser
    ): ReviewDto = reviewService.create(id, req, principal.username)

    @PutMapping("/reviews/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody req: CreateReviewRequest,
        @AuthenticationPrincipal principal: SpringUser
    ): ReviewDto = reviewService.update(id, req, principal.username)

    @DeleteMapping("/reviews/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable id: Long,
        @AuthenticationPrincipal principal: SpringUser
    ) = reviewService.delete(id, principal.username)

    @GetMapping("/bootcamps/{id}/review-summary")
    fun getSummary(@PathVariable id: Long): ReviewSummaryDto =
        reviewService.getSummary(id)

    @GetMapping("/rankings")
    fun getRankings(): List<RankingDto> =
        reviewService.getRankings()
}