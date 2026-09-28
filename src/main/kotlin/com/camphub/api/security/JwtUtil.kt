package com.camphub.api.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.nio.charset.StandardCharsets
import java.util.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class JwtUtil(
    @Value("\${app.jwt.secret}") private val secret: String,
    @Value("\${app.jwt.expiration-ms}") private val expirationMs: Long
) {
    private val key = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))

    fun generateToken(subject: String, extraClaims: Map<String, Any> = emptyMap()): String {
        val now = Date()
        val exp = Date(now.time + expirationMs)
        // pastikan extraClaims tidak menimpa reserved claims (sub/exp/iat)
        val safeClaims = extraClaims.filterKeys { it !in setOf("sub", "exp", "iat", "nbf", "jti") }
        return Jwts.builder()
            .subject(subject)
            .issuedAt(now)
            .expiration(exp)
            .claims(safeClaims)
            .signWith(key)
            .compact()
    }

    fun extractUsername(token: String): String =
        parseClaims(token).subject

    fun isTokenValid(token: String, username: String): Boolean {
        val claims = parseClaims(token)
        val notExpired = claims.expiration?.after(Date()) ?: false
        return notExpired && claims.subject == username
    }

    fun isExpired(token: String): Boolean =
        parseClaims(token).expiration?.before(Date()) ?: true

    private fun parseClaims(token: String): Claims =
        Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
}
