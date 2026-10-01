package com.camphub.api.dto

import com.camphub.api.model.Program
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size

// Request DTO (create and update)
data class CreateProgramRequest(
    @field:NotBlank(message = "Nama program wajib diisi")
    @field:Size(max = 150, message = "Nama program maksimal 150 karakter")
    val name: String,

    @field:NotBlank(message = "Kategori wajib diisi")
    @field:Size(max = 100, message = "Kategori maksimal 100 karakter")
    val category: String,

    @field:PositiveOrZero(message = "Harga tidak boleh negatif")
    val price: Long,

    @field:Min(value = 1, message = "Durasi minimal 1 minggu")
    val durationWeeks: Int,

    @field:NotBlank(message = "Silabus wajib diisi")
    @field:Size(max = 5000, message = "Silabus maksimal 5000 karakter")
    val syllabus: String,

    val registrationOpen: Boolean
)

// Response DTO
data class ProgramDto(
    val id: Long?,
    val bootcampId: Long?,
    val bootcampName: String,
    val name: String,
    val category: String,
    val price: Long,
    val durationWeeks: Int,
    val syllabus: String,
    val registrationOpen: Boolean
)

// Mapper entity → DTO
fun Program.toDto() =
    ProgramDto(
        id = this.id,
        bootcampId = this.bootcamp.id,
        bootcampName = this.bootcamp.name,
        name = this.name,
        category = this.category,
        price = this.price,
        durationWeeks = this.durationWeeks,
        syllabus = this.syllabus,
        registrationOpen = this.isRegistrationOpen
    )
