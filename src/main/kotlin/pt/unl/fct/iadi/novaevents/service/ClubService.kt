package pt.unl.fct.iadi.novaevents.service

import java.util.NoSuchElementException
import org.springframework.stereotype.Service
import pt.unl.fct.iadi.novaevents.model.Club
import pt.unl.fct.iadi.novaevents.repository.ClubRepository

@Service
class ClubService(private val clubRepository: ClubRepository) {

        fun findAll(): List<Club> = clubRepository.findAll()

        fun findById(id: Long): Club =
                clubRepository.findById(id).orElseThrow {
                        NoSuchElementException("Club with id $id was not found")
                }

        fun findEventCountsByClubId(): Map<Long, Long> =
                clubRepository.findEventCountsByClubId().associate { it.clubId to it.eventCount }
}
