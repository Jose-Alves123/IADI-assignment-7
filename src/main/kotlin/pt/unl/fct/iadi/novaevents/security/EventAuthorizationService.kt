package pt.unl.fct.iadi.novaevents.security

import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import pt.unl.fct.iadi.novaevents.repository.EventRepository

@Component("eventAuthorizationService")
class EventAuthorizationService(private val eventRepository: EventRepository) {

    fun canEdit(eventId: Long, authentication: Authentication?): Boolean {
        val username = authentication?.name ?: return false
        return eventRepository.existsByIdAndOwner_Username(eventId, username)
    }

    fun canDelete(eventId: Long, authentication: Authentication?): Boolean {
        val auth = authentication ?: return false
        val isAdmin = auth.authorities.any { it.authority == "ROLE_ADMIN" }
        return isAdmin || eventRepository.existsByIdAndOwner_Username(eventId, auth.name)
    }
}
