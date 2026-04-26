package pt.unl.fct.iadi.novaevents.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "clubs")
class Club(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long = 0,
        @Column(nullable = false, unique = true) var name: String = "",
        @Column(nullable = false, length = 2000) var description: String = "",
        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        var category: ClubCategory = ClubCategory.ACADEMIC
)
