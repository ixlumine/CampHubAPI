package com.camphub.api.dto

import com.camphub.api.model.Bootcamp
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

// Request DTO (create and update)
data class CreateBootcampRequest(
    @field:NotBlank(message = "Nama bootcamp wajib diisi")
    @field:Size(max = 150, message = "Nama bootcamp maksimal 150 karakter")
    val name: String,

    @field:NotBlank(message = "Deskripsi wajib diisi")
    @field:Size(max = 5000, message = "Deskripsi maksimal 5000 karakter")
    val description: String,

    @field:NotBlank(message = "Lokasi wajib diisi")
    @field:Size(max = 100, message = "Lokasi maksimal 100 karakter")
    val location: String,

    // Optional
    @field:Size(max = 255, message = "Website maksimal 255 karakter")
    val website: String? = null
)

// Response DTO
data class BootcampDto(
    val id: Long?,
    val name: String,
    val description: String,
    val location: String,
    val website: String?,
    val ownerId: Long?,
    val ownerName: String
)

// Mapper entity → DTO
fun Bootcamp.toDto() =
    BootcampDto(
        id = this.id,
        name = this.name,
        description = this.description,
        location = this.location,
        website = this.website,
        ownerId = this.owner.id,
        ownerName = this.owner.name
    )
