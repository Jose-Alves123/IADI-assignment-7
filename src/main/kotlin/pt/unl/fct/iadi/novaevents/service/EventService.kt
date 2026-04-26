package pt.unl.fct.iadi.novaevents.service

import java.util.NoSuchElementException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pt.unl.fct.iadi.novaevents.controller.dto.EventFormDto
import pt.unl.fct.iadi.novaevents.model.Event
import pt.unl.fct.iadi.novaevents.model.EventType
import pt.unl.fct.iadi.novaevents.repository.AppUserRepository
import pt.unl.fct.iadi.novaevents.repository.ClubRepository
import pt.unl.fct.iadi.novaevents.repository.EventRepository
import pt.unl.fct.iadi.novaevents.repository.EventTypeRepository

@Service
class EventService(
        private val eventRepository: EventRepository,
        private val clubRepository: ClubRepository,
        private val eventTypeRepository: EventTypeRepository,
        private val appUserRepository: AppUserRepository
) {

        fun findAll(filter: EventFilter): List<Event> {
                return eventRepository.findAllByFilter(
                        typeName = filter.type,
                        clubId = filter.clubId,
                        fromDate = filter.from,
                        toDate = filter.to
                )
        }

        fun findByIdAndClubId(clubId: Long, eventId: Long): Event {
                return eventRepository.findByIdAndClub_Id(eventId, clubId)
                        ?: throw NoSuchElementException(
                                "Event with id $eventId for club $clubId was not found"
                        )
        }

        fun findById(eventId: Long): Event {
                return eventRepository.findById(eventId).orElse(null)
                        ?: throw NoSuchElementException("Event with id $eventId was not found")
        }

        fun findByClubId(clubId: Long): List<Event> =
                eventRepository.findByClub_IdOrderByDate(clubId)

        fun findAllTypes(): List<EventType> = eventTypeRepository.findAll().sortedBy { it.name }

        @Transactional
        fun create(clubId: Long, form: EventFormDto): Event {
                validateUniqueName(form.name!!, null)
                val username =
                        SecurityContextHolder.getContext().authentication?.name
                                ?: throw IllegalStateException("Authenticated user is required")
                val club =
                        clubRepository.findById(clubId).orElseThrow {
                                NoSuchElementException("Club with id $clubId was not found")
                        }
                val type = resolveType(form.type!!)
                val owner =
                        appUserRepository.findByUsername(username)
                                ?: throw NoSuchElementException("User '$username' was not found")

                val event =
                        Event(
                                club = club,
                                name = form.name!!.trim(),
                                date = form.date!!,
                                location = normalizeOptionalText(form.location),
                                type = type,
                                owner = owner,
                                description = normalizeOptionalText(form.description)
                        )
                return eventRepository.save(event)
        }

        @Transactional
        fun update(clubId: Long, eventId: Long, form: EventFormDto): Event {
                val event = findByIdAndClubId(clubId, eventId)
                validateUniqueName(form.name!!, eventId)
                val type = resolveType(form.type!!)

                event.name = form.name!!.trim()
                event.date = form.date!!
                event.location = normalizeOptionalText(form.location)
                event.type = type
                event.description = normalizeOptionalText(form.description)

                return eventRepository.save(event)
        }

        @Transactional
        fun delete(clubId: Long, eventId: Long) {
                val event = eventRepository.findByIdAndClub_Id(eventId, clubId)
                if (event == null) {
                        throw NoSuchElementException(
                                "Event with id $eventId for club $clubId was not found"
                        )
                }
                eventRepository.delete(event)
        }

        private fun validateUniqueName(rawName: String, currentEventId: Long?) {
                val normalized = rawName.trim()
                val duplicate =
                        if (currentEventId == null) {
                                eventRepository.existsByNameIgnoreCase(normalized)
                        } else {
                                eventRepository.existsByNameIgnoreCaseAndIdNot(
                                        normalized,
                                        currentEventId
                                )
                        }
                if (duplicate) {
                        throw DuplicateEventNameException("An event with this name already exists")
                }
        }

        private fun resolveType(typeName: String): EventType =
                eventTypeRepository.findByNameIgnoreCase(typeName.trim())
                        ?: throw NoSuchElementException("Event type '$typeName' was not found")

        private fun normalizeOptionalText(value: String?): String? {
                val trimmed = value?.trim().orEmpty()
                return trimmed.ifBlank { null }
        }
}
