package pt.unl.fct.iadi.novaevents.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.Date
import javax.crypto.SecretKey
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class JwtService(
        @Value(
                "\${app.security.jwt-secret:bm92YWV2ZW50cy1qd3Qtc2VjcmV0LW5vdC1mb3ItcHJvZHVjdGlvbi0xMjM0NTY3ODkw}"
        )
        private val secretBase64: String,
        @Value("\${app.security.jwt-expiration-seconds:28800}") private val expirationSeconds: Long
) {
    private val signingKey: SecretKey =
            try {
                Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretBase64))
            } catch (_: IllegalArgumentException) {
                Keys.hmacShaKeyFor(secretBase64.toByteArray(StandardCharsets.UTF_8))
            }

    fun generateToken(username: String, roles: List<String>): String {
        val now = Instant.now()
        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(signingKey)
                .compact()
    }

    fun parseClaims(token: String): Claims {
        return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).payload
    }
}
