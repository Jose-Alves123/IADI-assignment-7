package pt.unl.fct.iadi.novaevents.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "app_users")
class AppUser(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long = 0,
        @Column(nullable = false, unique = true) var username: String = "",
        @Column(nullable = false) var passwordHash: String = "",
        @OneToMany(
                mappedBy = "user",
                cascade = [CascadeType.ALL],
                orphanRemoval = true,
                fetch = FetchType.EAGER
        )
        var roles: MutableSet<AppUserRole> = mutableSetOf()
) {
    fun setRoles(roleNames: Collection<AppRoleName>) {
        roles.clear()
        roles.addAll(roleNames.map { AppUserRole(user = this, role = it) })
    }
}
