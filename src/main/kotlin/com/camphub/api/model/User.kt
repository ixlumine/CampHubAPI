package com.camphub.api.model

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

@Entity
@Table(name = "users", uniqueConstraints = [UniqueConstraint(columnNames = ["email"])])
class User : BaseEntity() {
    @field:NotBlank @Column(nullable = false) var name: String = ""

    @field:Email @field:NotBlank @Column(nullable = false, unique = true) var email: String = ""

    // BCrypt hash, never plain text
    @field:NotBlank @Column(nullable = false) var password: String = ""

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: Role = Role.USER
}
