package com.camphub.api.model

import jakarta.persistence.*
import jakarta.validation.constraints.*

@Entity
@Table(name = "programs")
class Program : BaseEntity() {
    // Parent bootcamp
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bootcamp_id", nullable = false)
    lateinit var bootcamp: Bootcamp

    @field:NotBlank
    @Column(nullable = false)
    var name: String = ""

    @field:NotBlank
    @Column(nullable = false)
    var category: String = ""

    // In rupiah
    @field:PositiveOrZero
    @Column(nullable = false)
    var price: Long = 0

    @field:Min(1)
    @Column(name = "duration_weeks", nullable = false)
    var durationWeeks: Int = 1

    // Long text: length 65535 makes it TEXT in MySQL
    @Lob
    @field:NotBlank
    @Column(nullable = false, length = 65535)
    var syllabus: String = ""

    // Sent as "registrationOpen" in JSON
    @Column(name = "registration_open", nullable = false)
    var isRegistrationOpen: Boolean = true
}
