package com.camphub.api.controller

import com.camphub.api.dto.CreateProgramRequest
import com.camphub.api.dto.ProgramDto
import com.camphub.api.service.ProgramService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User as SpringUser
import org.springframework.web.bind.annotation.*

// Program endpoints; nested under a bootcamp for list and create
@RestController
@RequestMapping("/api")
class ProgramController(private val service: ProgramService) {

    @GetMapping("/bootcamps/{bootcampId}/programs")
    fun listByBootcamp(@PathVariable bootcampId: Long): List<ProgramDto> = service.listByBootcamp(bootcampId)

    @GetMapping("/programs/{id}")
    fun get(@PathVariable id: Long): ProgramDto = service.get(id)

    @PostMapping("/bootcamps/{bootcampId}/programs")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @PathVariable bootcampId: Long,
        @Valid @RequestBody req: CreateProgramRequest,
        @AuthenticationPrincipal principal: SpringUser
    ): ProgramDto = service.create(bootcampId, req, principal.username)

    @PutMapping("/programs/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody req: CreateProgramRequest,
        @AuthenticationPrincipal principal: SpringUser
    ): ProgramDto = service.update(id, req, principal.username)

    @DeleteMapping("/programs/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long, @AuthenticationPrincipal principal: SpringUser) =
        service.delete(id, principal.username)
}
