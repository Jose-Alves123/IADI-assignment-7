package pt.unl.fct.iadi.novaevents.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import pt.unl.fct.iadi.novaevents.model.Club

interface ClubRepository : JpaRepository<Club, Long> {

    interface ClubEventCountProjection {
        val clubId: Long
        val eventCount: Long
    }

    @Query(
            """
            select c.id as clubId, count(e.id) as eventCount
            from Club c
            left join Event e on e.club = c
            group by c.id
            """
    )
    fun findEventCountsByClubId(): List<ClubEventCountProjection>
}
