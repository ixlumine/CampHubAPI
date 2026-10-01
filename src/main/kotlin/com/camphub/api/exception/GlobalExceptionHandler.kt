package com.camphub.api.exception

import jakarta.servlet.http.HttpServletRequest
import java.time.Instant
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.resource.NoResourceFoundException

/**
 * Format response error yang konsisten untuk seluruh endpoint.
 */
data class ApiErrorResponse(
    val timestamp: Instant = Instant.now(),
    val status: Int,
    val error: String,
    val message: String,
    val path: String,
    val details: Any? = null
)

/**
 * Custom exceptions agar service layer bisa melempar error sesuai konteks bisnis.
 */
class BadRequestException(message: String) : RuntimeException(message)
class NotFoundException(message: String) : RuntimeException(message)
class ForbiddenException(message: String) : RuntimeException(message)
class UnauthorizedException(message: String = "Belum login") : RuntimeException(message)

/**
 * Global handler untuk mengubah exception menjadi response JSON yang rapi dan konsisten.
 */
@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(ex: BadRequestException, req: HttpServletRequest): ResponseEntity<ApiErrorResponse> {
        log.warn("BAD REQUEST [{} {}] - {}", req.method, req.requestURI, ex.message)
        return build(HttpStatus.BAD_REQUEST, ex.message ?: "Bad request", req)
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException, req: HttpServletRequest): ResponseEntity<ApiErrorResponse> {
        log.warn("NOT FOUND [{} {}] - {}", req.method, req.requestURI, ex.message)
        return build(HttpStatus.NOT_FOUND, ex.message ?: "Data tidak ditemukan", req)
    }

    @ExceptionHandler(ForbiddenException::class)
    fun handleForbidden(ex: ForbiddenException, req: HttpServletRequest): ResponseEntity<ApiErrorResponse> {
        log.warn("FORBIDDEN [{} {}] - {}", req.method, req.requestURI, ex.message)
        return build(HttpStatus.FORBIDDEN, ex.message ?: "Akses ditolak", req)
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorized(ex: UnauthorizedException, req: HttpServletRequest): ResponseEntity<ApiErrorResponse> {
        log.warn("UNAUTHORIZED [{} {}] - {}", req.method, req.requestURI, ex.message)
        return build(HttpStatus.UNAUTHORIZED, ex.message ?: "Unauthorized", req)
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthException(ex: AuthenticationException, req: HttpServletRequest): ResponseEntity<ApiErrorResponse> {
        log.warn("AUTHENTICATION FAILED [{} {}] - {}", req.method, req.requestURI, ex.message)
        return build(HttpStatus.UNAUTHORIZED, "Belum login", req)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: AccessDeniedException, req: HttpServletRequest): ResponseEntity<ApiErrorResponse> {
        log.warn("FORBIDDEN [{} {}] - {}", req.method, req.requestURI, ex.message)
        return build(HttpStatus.FORBIDDEN, "Akses ditolak", req)
    }

    // Fallback only: services check relations and duplicates first
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrity(
        ex: DataIntegrityViolationException,
        req: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        log.warn("DATA INTEGRITY [{} {}] - {}", req.method, req.requestURI, ex.mostSpecificCause.message)
        return build(HttpStatus.BAD_REQUEST, "Data melanggar aturan relasi atau duplikat", req)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(
        ex: MethodArgumentNotValidException,
        req: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        val errors = ex.bindingResult.allErrors.map { err ->
            val fe = err as? FieldError
            mapOf(
                "field" to (fe?.field ?: "unknown"),
                "message" to (err.defaultMessage ?: "invalid")
            )
        }
        log.warn("VALIDATION ERROR [{} {}] - {}", req.method, req.requestURI, errors)
        val body = ApiErrorResponse(
            status = HttpStatus.BAD_REQUEST.value(),
            error = HttpStatus.BAD_REQUEST.reasonPhrase,
            message = "Validasi gagal",
            path = req.requestURI,
            details = errors
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleNotReadable(
        ex: HttpMessageNotReadableException,
        req: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        log.warn("BAD REQUEST BODY [{} {}] - {}", req.method, req.requestURI, ex.mostSpecificCause.message)
        return build(HttpStatus.BAD_REQUEST, "Format data tidak valid", req)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(
        ex: MethodArgumentTypeMismatchException,
        req: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        log.warn("TYPE MISMATCH [{} {}] - {}", req.method, req.requestURI, ex.message)
        return build(HttpStatus.BAD_REQUEST, "Parameter ${ex.name} tidak valid", req)
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResource(ex: NoResourceFoundException, req: HttpServletRequest): ResponseEntity<ApiErrorResponse> {
        log.warn("NO ENDPOINT [{} {}]", req.method, req.requestURI)
        return build(HttpStatus.NOT_FOUND, "Endpoint tidak ditemukan", req)
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotSupported(
        ex: HttpRequestMethodNotSupportedException,
        req: HttpServletRequest
    ): ResponseEntity<ApiErrorResponse> {
        log.warn("METHOD NOT ALLOWED [{} {}]", req.method, req.requestURI)
        return build(HttpStatus.METHOD_NOT_ALLOWED, "Method tidak didukung", req)
    }

    @ExceptionHandler(Exception::class)
    fun handleInternal(ex: Exception, req: HttpServletRequest): ResponseEntity<ApiErrorResponse> {
        log.error(
            "INTERNAL SERVER ERROR [{} {}] - {} ({})",
            req.method,
            req.requestURI,
            ex.message,
            ex::class.qualifiedName,
            ex
        )
        ex.cause?.let {
            log.error("ROOT CAUSE: {} ({})", it.message, it::class.qualifiedName, it)
        }
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Terjadi kesalahan pada server", req)
    }

    private fun build(status: HttpStatus, message: String, req: HttpServletRequest): ResponseEntity<ApiErrorResponse> {
        val body = ApiErrorResponse(
            status = status.value(),
            error = status.reasonPhrase,
            message = message,
            path = req.requestURI
        )
        return ResponseEntity.status(status).body(body)
    }
}
