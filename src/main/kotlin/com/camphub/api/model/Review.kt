package com.camphub.api.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "reviews",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_reviews_bootcamp_author", columnNames = ["bootcamp_id", "author_id"])
    ]
)
class Review : BaseEntity() {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bootcamp_id", nullable = false)
    lateinit var bootcamp: Bootcamp

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    lateinit var author: User

    @Column(nullable = false)
    var rating: Int = 0

    @Lob
    @Column(nullable = false, length = 65535)
    var content: String = ""

    @Enumerated(EnumType.STRING)
    @Column(name = "career_status", nullable = false)
    lateinit var careerStatus: CareerStatus

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
}