package pt.unl.fct.iadi.novaevents.repository

import java.time.LocalDate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import pt.unl.fct.iadi.novaevents.model.Event

interface EventRepository : JpaRepository<Event, Long> {
        fun existsByNameIgnoreCase(name: String): Boolean

        fun existsByNameIgnoreCaseAndIdNot(name: String, id: Long): Boolean

        fun findByIdAndClub_Id(id: Long, clubId: Long): Event?

        fun findByClub_IdOrderByDate(clubId: Long): List<Event>

        fun existsByIdAndOwner_Username(id: Long, username: String): Boolean

        @Query(
                """
            select e
            from Event e
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
