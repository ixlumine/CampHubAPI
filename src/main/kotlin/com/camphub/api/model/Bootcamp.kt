package com.camphub.api.model

import jakarta.persistence.*
import jakarta.validation.constraints.*

@Entity
@Table(name = "bootcamps")
class Bootcamp : BaseEntity() {
    @field:NotBlank
    @Column(nullable = false)
    var name: String = ""

    @Lob
    @field:NotBlank
    @Column(nullable = false, length = 65535)
    var description: String = ""

    @field:NotBlank
    @Column(nullable = false)
    var location: String = ""
    var website: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    lateinit var owner: User
}
