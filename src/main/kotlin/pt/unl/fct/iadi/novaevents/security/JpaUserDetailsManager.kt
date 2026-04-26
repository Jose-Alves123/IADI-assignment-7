package pt.unl.fct.iadi.novaevents.security

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.provisioning.UserDetailsManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pt.unl.fct.iadi.novaevents.model.AppRoleName
import pt.unl.fct.iadi.novaevents.model.AppUser
import pt.unl.fct.iadi.novaevents.repository.AppUserRepository

@Service
class JpaUserDetailsManager(private val appUserRepository: AppUserRepository) : UserDetailsManager {

    override fun userExists(username: String): Boolean =
            appUserRepository.existsByUsername(username)

    override fun loadUserByUsername(username: String): UserDetails {
        val user =
                appUserRepository.findByUsername(username)
                        ?: throw org.springframework.security.core.userdetails
                                .UsernameNotFoundException("User '$username' not found")

        val authorities = user.roles.map { SimpleGrantedAuthority(it.role.name) }
        return User.withUsername(user.username)
                .password(user.passwordHash)
                .authorities(authorities)
                .build()
    }

    @Transactional
    override fun createUser(user: UserDetails) {
        val entity = AppUser(username = user.username, passwordHash = user.password)
        val roles =
                user.authorities.mapNotNull {
                    runCatching { AppRoleName.valueOf(it.authority) }.getOrNull()
                }
        entity.setRoles(roles)
        appUserRepository.save(entity)
    }

    @Transactional
    override fun updateUser(user: UserDetails) {
        val existing =
                appUserRepository.findByUsername(user.username)
                        ?: throw org.springframework.security.core.userdetails
                                .UsernameNotFoundException("User '${user.username}' not found")
        existing.passwordHash = user.password
        val roles =
                user.authorities.mapNotNull {
                    runCatching { AppRoleName.valueOf(it.authority) }.getOrNull()
                }
        existing.setRoles(roles)
        appUserRepository.save(existing)
    }

    @Transactional
    override fun deleteUser(username: String) {
        val existing = appUserRepository.findByUsername(username) ?: return
        appUserRepository.delete(existing)
    }

    @Transactional
    override fun changePassword(oldPassword: String?, newPassword: String?) {
        throw UnsupportedOperationException(
                "Password change flow is not implemented for this assignment"
        )
    }
}
