package pt.unl.fct.iadi.novaevents.security

import java.util.Base64
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class JwtServiceTest {

    @Test
    fun generateAndParseTokenWithBase64Secret() {
        val secret =
                Base64.getEncoder().encodeToString("01234567890123456789012345678901".toByteArray())
        val service = JwtService(secret, 3600)

        val token = service.generateToken("alice", listOf("ROLE_EDITOR"))
        val claims = service.parseClaims(token)

        assertEquals("alice", claims.subject)
        assertEquals(listOf("ROLE_EDITOR"), claims["roles", List::class.java])
    }

    @Test
    fun generateAndParseTokenWithPlainTextSecretFallback() {
        val service = JwtService("plain-text-secret-plain-text-secret-plain-text-secret", 3600)

        val token = service.generateToken("bob", listOf("ROLE_ADMIN"))
        val claims = service.parseClaims(token)

        assertEquals("bob", claims.subject)
        assertEquals(listOf("ROLE_ADMIN"), claims["roles", List::class.java])
    }
}
