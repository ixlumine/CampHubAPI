package com.camphub.api.service

import com.camphub.api.dto.CreateProgramRequest
import com.camphub.api.dto.ProgramDto
import com.camphub.api.dto.toDto
import com.camphub.api.exception.ForbiddenException
import com.camphub.api.exception.NotFoundException
import com.camphub.api.exception.UnauthorizedException
import com.camphub.api.model.Bootcamp
import com.camphub.api.model.Program
import com.camphub.api.model.Role
import com.camphub.api.model.User
import com.camphub.api.repository.BootcampRepository
import com.camphub.api.repository.ProgramRepository
import com.camphub.api.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProgramService(
    private val programRepository: ProgramRepository,
    private val bootcampRepository: BootcampRepository,
    private val userRepository: UserRepository
) {
    // 404 if the bootcamp does not exist
    @Transactional(readOnly = true)
    fun listByBootcamp(bootcampId: Long): List<ProgramDto> {
        findBootcamp(bootcampId)
        return programRepository.findByBootcampIdOrderByIdAsc(bootcampId).map { it.toDto() }
    }

    @Transactional(readOnly = true)
    fun get(id: Long): ProgramDto = findProgram(id).toDto()

    // Only the owner of the bootcamp can add programs
    @Transactional
    fun create(bootcampId: Long, req: CreateProgramRequest, email: String): ProgramDto {
        val b = findBootcamp(bootcampId)
        if (b.owner.id != currentUser(email).id) {
            throw ForbiddenException("Hanya pemilik bootcamp yang dapat menambah program")
        }
        val p = Program().apply {
            bootcamp = b
            name = req.name.trim()
            category = req.category.trim()
            price = req.price
            durationWeeks = req.durationWeeks
            syllabus = req.syllabus.trim()
            isRegistrationOpen = req.registrationOpen
        }
        programRepository.save(p)
        return p.toDto()
    }

    // Only the owner of the bootcamp can update
    @Transactional
    fun update(id: Long, req: CreateProgramRequest, email: String): ProgramDto {
        val p = findProgram(id)
        if (p.bootcamp.owner.id != currentUser(email).id) {
            throw ForbiddenException("Hanya pemilik bootcamp yang dapat mengubah program ini")
        }
        p.apply {
            name = req.name.trim()
            category = req.category.trim()
            price = req.price
            durationWeeks = req.durationWeeks
            syllabus = req.syllabus.trim()
            isRegistrationOpen = req.registrationOpen
        }
        return p.toDto()
    }

    // Bootcamp owner or admin can delete
    @Transactional
    fun delete(id: Long, email: String) {
        val p = findProgram(id)
        val user = currentUser(email)
        if (p.bootcamp.owner.id != user.id && user.role != Role.ADMIN) {
            throw ForbiddenException("Hanya pemilik bootcamp atau admin yang dapat menghapus program ini")
        }
        programRepository.delete(p)
    }

    private fun findBootcamp(id: Long): Bootcamp =
        bootcampRepository.findById(id).orElseThrow { NotFoundException("Bootcamp id=$id tidak ditemukan") }

    private fun findProgram(id: Long): Program =
        programRepository.findById(id).orElseThrow { NotFoundException("Program id=$id tidak ditemukan") }

    private fun currentUser(email: String): User =
        userRepository.findByEmail(email) ?: throw UnauthorizedException()
}
