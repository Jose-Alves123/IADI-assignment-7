package pt.unl.fct.iadi.novaevents.repository

import java.time.LocalDate
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import pt.unl.fct.iadi.novaevents.model.Event

interface EventRepository : JpaRepository<Event, Long> {
        fun existsByNameIgnoreCase(name: String): Boolean

        fun existsByNameIgnoreCaseAndIdNot(name: String, id: Long): Boolean

        @EntityGraph(attributePaths = ["club", "type", "owner"])
        override fun findById(id: Long): java.util.Optional<Event>

        @EntityGraph(attributePaths = ["club", "type", "owner"])
        fun findByIdAndClub_Id(id: Long, clubId: Long): Event?

        @EntityGraph(attributePaths = ["club", "type", "owner"])
        fun findByClub_IdOrderByDate(clubId: Long): List<Event>

        fun existsByIdAndOwner_Username(id: Long, username: String): Boolean

        @EntityGraph(attributePaths = ["club", "type", "owner"])
        @Query(
                """
            select distinct e
            from Event e
            join fetch e.club
            join fetch e.type
            join fetch e.owner
            where (:typeName is null or lower(e.type.name) = lower(:typeName))
              and (:clubId is null or e.club.id = :clubId)
              and (:fromDate is null or e.date >= :fromDate)
              and (:toDate is null or e.date <= :toDate)
            order by e.date
            """
        )
        fun findAllByFilter(
                @Param("typeName") typeName: String?,
                @Param("clubId") clubId: Long?,
                @Param("fromDate") fromDate: LocalDate?,
                @Param("toDate") toDate: LocalDate?
        ): List<Event>
}
