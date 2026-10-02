package com.camphub.api.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank

@Entity
@Table(name = "forum_posts")
class ForumPost : BaseEntity() {
    @field:NotBlank
    @Column(nullable = false)
    var title: String = ""

    @Lob
    @field:NotBlank
    @Column(nullable = false, length = 65535)
    var content: String = ""

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    lateinit var user: User
}
