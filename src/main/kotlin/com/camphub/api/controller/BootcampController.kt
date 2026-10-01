package com.camphub.api.controller

import com.camphub.api.dto.BootcampDto
import com.camphub.api.dto.CreateBootcampRequest
import com.camphub.api.service.BootcampService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User as SpringUser
import org.springframework.web.bind.annotation.*

// Bootcamp endpoints; login required
@RestController
@RequestMapping("/api/bootcamps")
class BootcampController(private val service: BootcampService) {

    @GetMapping
    fun list(): List<BootcampDto> = service.list()

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): BootcampDto = service.get(id)

    // Logged-in user's email comes from the token
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Valid @RequestBody req: CreateBootcampRequest,
        @AuthenticationPrincipal principal: SpringUser
    ): BootcampDto = service.create(req, principal.username)

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody req: CreateBootcampRequest,
        @AuthenticationPrincipal principal: SpringUser
    ): BootcampDto = service.update(id, req, principal.username)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long, @AuthenticationPrincipal principal: SpringUser) =
        service.delete(id, principal.username)
}
