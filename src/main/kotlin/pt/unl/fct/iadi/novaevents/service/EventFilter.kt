package pt.unl.fct.iadi.novaevents.service

import java.time.LocalDate

data class EventFilter(
        val type: String? = null,
        val clubId: Long? = null,
        val from: LocalDate? = null,
        val to: LocalDate? = null
)
