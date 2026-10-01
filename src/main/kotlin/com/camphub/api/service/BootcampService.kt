package com.camphub.api.service

import com.camphub.api.dto.BootcampDto
import com.camphub.api.dto.CreateBootcampRequest
import com.camphub.api.dto.toDto
import com.camphub.api.exception.BadRequestException
import com.camphub.api.exception.ForbiddenException
import com.camphub.api.exception.NotFoundException
import com.camphub.api.exception.UnauthorizedException
import com.camphub.api.model.Bootcamp
import com.camphub.api.model.Role
import com.camphub.api.model.User
import com.camphub.api.repository.BootcampRepository
import com.camphub.api.repository.ProgramRepository
import com.camphub.api.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BootcampService(
    private val bootcampRepository: BootcampRepository,
    private val programRepository: ProgramRepository,
    private val userRepository: UserRepository
) {
    @Transactional(readOnly = true)
    fun list(): List<BootcampDto> = bootcampRepository.findAllByOrderByNameAsc().map { it.toDto() }

    @Transactional(readOnly = true)
    fun get(id: Long): BootcampDto = findBootcamp(id).toDto()

    // Owner is the logged-in provider
    @Transactional
    fun create(req: CreateBootcampRequest, email: String): BootcampDto {
        val b = Bootcamp().apply {
            name = req.name.trim()
            description = req.description.trim()
            location = req.location.trim()
            website = req.website?.trim()?.ifBlank { null }
            owner = currentUser(email)
        }
        bootcampRepository.save(b)
        return b.toDto()
    }

    // Only the owner can update
    @Transactional
    fun update(id: Long, req: CreateBootcampRequest, email: String): BootcampDto {
        val b = findBootcamp(id)
        if (b.owner.id != currentUser(email).id) {
            throw ForbiddenException("Hanya pemilik yang dapat mengubah bootcamp ini")
        }
        b.apply {
            name = req.name.trim()
            description = req.description.trim()
            location = req.location.trim()
            website = req.website?.trim()?.ifBlank { null }
        }
        // No save() needed: the entity is managed inside the transaction
        return b.toDto()
    }

    // Owner or admin can delete; blocked while programs exist
    @Transactional
    fun delete(id: Long, email: String) {
        val b = findBootcamp(id)
        val user = currentUser(email)
        if (b.owner.id != user.id && user.role != Role.ADMIN) {
            throw ForbiddenException("Hanya pemilik atau admin yang dapat menghapus bootcamp ini")
        }
        if (programRepository.existsByBootcampId(id)) {
            throw BadRequestException("Bootcamp tidak dapat dihapus karena masih memiliki program")
        }
        bootcampRepository.delete(b)
    }

    private fun findBootcamp(id: Long): Bootcamp =
        bootcampRepository.findById(id).orElseThrow { NotFoundException("Bootcamp id=$id tidak ditemukan") }

    private fun currentUser(email: String): User =
        userRepository.findByEmail(email) ?: throw UnauthorizedException()
}
