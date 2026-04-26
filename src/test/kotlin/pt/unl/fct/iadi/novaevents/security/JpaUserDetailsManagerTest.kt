package pt.unl.fct.iadi.novaevents.security

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import pt.unl.fct.iadi.novaevents.model.AppRoleName
import pt.unl.fct.iadi.novaevents.model.AppUser
import pt.unl.fct.iadi.novaevents.model.AppUserRole
import pt.unl.fct.iadi.novaevents.repository.AppUserRepository

class JpaUserDetailsManagerTest {

    private val repository = Mockito.mock(AppUserRepository::class.java)
    private val manager = JpaUserDetailsManager(repository)

    @Test
    fun userExistsDelegatesToRepository() {
        Mockito.`when`(repository.existsByUsername("alice")).thenReturn(true)

        assertTrue(manager.userExists("alice"))
        assertFalse(manager.userExists("bob"))
    }

    @Test
    fun loadUserByUsernameMapsRoles() {
        val user = AppUser(username = "alice", passwordHash = "hash")
        user.roles =
                mutableSetOf(
                        AppUserRole(user = user, role = AppRoleName.ROLE_EDITOR),
                        AppUserRole(user = user, role = AppRoleName.ROLE_ADMIN)
                )
        Mockito.`when`(repository.findByUsername("alice")).thenReturn(user)

        val loaded = manager.loadUserByUsername("alice")

        assertEquals("alice", loaded.username)
        assertEquals("hash", loaded.password)
        assertEquals(
                setOf(SimpleGrantedAuthority("ROLE_EDITOR"), SimpleGrantedAuthority("ROLE_ADMIN")),
                loaded.authorities.toSet()
        )
    }

    @Test
    fun createUserStoresMappedRoles() {
        val captor = ArgumentCaptor.forClass(AppUser::class.java)
        val user =
                User.withUsername("carol")
                        .password("secret")
                        .authorities(
                                SimpleGrantedAuthority("ROLE_EDITOR"),
                                SimpleGrantedAuthority("ROLE_UNKNOWN")
                        )
                        .build()

        manager.createUser(user)

        Mockito.verify(repository).save(captor.capture())
        assertEquals("carol", captor.value.username)
        assertEquals("secret", captor.value.passwordHash)
        assertEquals(setOf(AppRoleName.ROLE_EDITOR), captor.value.roles.map { it.role }.toSet())
    }

    @Test
    fun updateUserReplacesPasswordAndRoles() {
        val existing = AppUser(username = "alice", passwordHash = "old")
        existing.setRoles(listOf(AppRoleName.ROLE_ADMIN))
        Mockito.`when`(repository.findByUsername("alice")).thenReturn(existing)
        val user =
                User.withUsername("alice")
                        .password("new")
                        .authorities(SimpleGrantedAuthority("ROLE_EDITOR"))
                        .build()

        manager.updateUser(user)

        Mockito.verify(repository).save(existing)
        assertEquals("new", existing.passwordHash)
        assertEquals(setOf(AppRoleName.ROLE_EDITOR), existing.roles.map { it.role }.toSet())
    }

    @Test
    fun deleteUserRemovesExistingRecord() {
        val existing = AppUser(username = "alice", passwordHash = "hash")
        Mockito.`when`(repository.findByUsername("alice")).thenReturn(existing)

        manager.deleteUser("alice")

        Mockito.verify(repository).delete(existing)
    }

    @Test
    fun changePasswordIsNotSupported() {
        assertThrows(UnsupportedOperationException::class.java) {
            manager.changePassword("old", "new")
        }
    }
}
