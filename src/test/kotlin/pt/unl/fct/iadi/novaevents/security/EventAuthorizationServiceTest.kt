package pt.unl.fct.iadi.novaevents.security

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import pt.unl.fct.iadi.novaevents.repository.EventRepository

class EventAuthorizationServiceTest {

    @Test
    fun canEditChecksOwnership() {
        val repository = mock(EventRepository::class.java)
        val service = EventAuthorizationService(repository)
        val authentication = UsernamePasswordAuthenticationToken("alice", "", emptyList())

        `when`(repository.existsByIdAndOwner_Username(3L, "alice")).thenReturn(true)

        assertTrue(service.canEdit(3L, authentication))
        assertFalse(service.canEdit(3L, null))
    }

    @Test
    fun canDeleteAllowsAdminOrOwner() {
        val repository = mock(EventRepository::class.java)
        val service = EventAuthorizationService(repository)
        val ownerAuth = UsernamePasswordAuthenticationToken("alice", "", emptyList())
        val adminAuth =
                UsernamePasswordAuthenticationToken(
                        "bob",
                        "",
                        listOf(SimpleGrantedAuthority("ROLE_ADMIN"))
                )

        `when`(repository.existsByIdAndOwner_Username(3L, "alice")).thenReturn(true)

        assertTrue(service.canDelete(3L, ownerAuth))
        assertTrue(service.canDelete(3L, adminAuth))
        assertFalse(service.canDelete(3L, null))
    }
}
