package com.camphub.api.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

@Entity
@Table(name = "forum_threads")
class ForumThread : BaseEntity() {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    lateinit var author: User

    @field:NotBlank
    @Column(nullable = false)
    var title: String = ""

    @Lob
    @field:NotBlank
    @Column(nullable = false, length = 65535)
    var content: String = ""

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
}
