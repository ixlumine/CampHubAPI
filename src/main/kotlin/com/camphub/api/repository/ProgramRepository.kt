package com.camphub.api.repository

import com.camphub.api.model.Program
import org.springframework.data.jpa.repository.JpaRepository

interface ProgramRepository : JpaRepository<Program, Long> {
    // Programs of one bootcamp, oldest first
    fun findByBootcampIdOrderByIdAsc(bootcampId: Long): List<Program>

    // Used to block deleting a bootcamp that still has programs
    fun existsByBootcampId(bootcampId: Long): Boolean
}
