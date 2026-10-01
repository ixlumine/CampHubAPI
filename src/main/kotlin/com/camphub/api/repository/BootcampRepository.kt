package com.camphub.api.repository

import com.camphub.api.model.Bootcamp
import org.springframework.data.jpa.repository.JpaRepository

interface BootcampRepository : JpaRepository<Bootcamp, Long> {
    // Sorted by name (A–Z)
    fun findAllByOrderByNameAsc(): List<Bootcamp>
}
