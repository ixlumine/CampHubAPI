package com.camphub.api.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

@Entity
@Table(name = "forum_comments")
class ForumComment : BaseEntity() {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thread_id", nullable = false)
    lateinit var thread: ForumThread

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    lateinit var author: User

    @Lob
    @field:NotBlank
    @Column(nullable = false, length = 65535)
    var content: String = ""

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
}
