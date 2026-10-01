package com.camphub.api.security

import com.camphub.api.exception.ApiErrorResponse
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

// 401 response in the same JSON format as other errors
@Component
class RestAuthEntryPoint(private val mapper: ObjectMapper) : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE

        val msg = if (request.getAttribute("jwt_invalid") == true)
            "Token invalid atau expired"
        else
            "Belum login"

        val body = ApiErrorResponse(
            status = 401,
            error = "Unauthorized",
            message = msg,
            path = request.requestURI
        )
        response.writer.write(mapper.writeValueAsString(body))
    }
}
