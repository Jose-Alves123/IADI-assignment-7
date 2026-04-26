package pt.unl.fct.iadi.novaevents.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "events")
class Event(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long = 0,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "club_id", nullable = false)
        var club: Club = Club(),
        @Column(nullable = false) var name: String = "",
        @Column(nullable = false) var date: LocalDate = LocalDate.now(),
        var location: String? = null,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "type_id", nullable = false)
        var type: EventType = EventType(),
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "owner_id", nullable = false)
        var owner: AppUser = AppUser(),
        var description: String? = null
) {
        val clubId: Long
                get() = club.id
}
