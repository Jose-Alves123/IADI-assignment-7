package pt.unl.fct.iadi.novaevents.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "event_types")
class EventType(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long = 0,
        @Column(nullable = false, unique = true) var name: String = ""
) {
    override fun toString(): String = name
}
