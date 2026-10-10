package com.camphub.api.repository

import com.camphub.api.model.Review
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReviewRepository : JpaRepository<Review, Long> {
    fun findByBootcampIdOrderByCreatedAtDesc(bootcampId: Long): List<Review>
    fun findByBootcampId(bootcampId: Long): List<Review>
    fun existsByBootcampIdAndAuthorId(bootcampId: Long, authorId: Long): Boolean
    fun existsByBootcampId(bootcampId: Long): Boolean
    fun countByBootcampId(bootcampId: Long): Long
}