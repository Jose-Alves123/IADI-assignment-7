package pt.unl.fct.iadi.novaevents.service

import java.time.LocalDate
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import pt.unl.fct.iadi.novaevents.model.AppRoleName
import pt.unl.fct.iadi.novaevents.model.AppUser
import pt.unl.fct.iadi.novaevents.model.Club
import pt.unl.fct.iadi.novaevents.model.ClubCategory
import pt.unl.fct.iadi.novaevents.model.Event
import pt.unl.fct.iadi.novaevents.model.EventType
import pt.unl.fct.iadi.novaevents.repository.AppUserRepository
import pt.unl.fct.iadi.novaevents.repository.ClubRepository
import pt.unl.fct.iadi.novaevents.repository.EventRepository
import pt.unl.fct.iadi.novaevents.repository.EventTypeRepository

@Component
class DataInitializer(
        private val appUserRepository: AppUserRepository,
        private val eventTypeRepository: EventTypeRepository,
        private val clubRepository: ClubRepository,
        private val eventRepository: EventRepository,
        private val passwordEncoder: PasswordEncoder
) : ApplicationRunner {

        override fun run(args: ApplicationArguments) {
                seedUsersIfMissing()

                if (eventTypeRepository.count() > 0 ||
                                clubRepository.count() > 0 ||
                                eventRepository.count() > 0
                ) {
                        return
                }

                val ownerAlice = appUserRepository.findByUsername("alice")!!
                val ownerBob = appUserRepository.findByUsername("bob")!!
                val ownerCharlie = appUserRepository.findByUsername("charlie")!!

                val types =
                        eventTypeRepository.saveAll(
                                listOf(
                                        EventType(name = "WORKSHOP"),
                                        EventType(name = "TALK"),
                                        EventType(name = "COMPETITION"),
                                        EventType(name = "SOCIAL"),
                                        EventType(name = "MEETING"),
                                        EventType(name = "OTHER")
                                )
                        )
                val typeByName = types.associateBy { it.name }

                val clubs =
                        clubRepository.saveAll(
                                listOf(
                                        Club(
                                                name = "Chess Club",
                                                description =
                                                        "A community for strategy lovers, from beginners to tournament players.",
                                                category = ClubCategory.ACADEMIC
                                        ),
                                        Club(
                                                name = "Robotics Club",
                                                description =
                                                        "The Robotics Club is the place to turn ideas into machines, build prototypes, and compete in engineering challenges.",
                                                category = ClubCategory.TECHNOLOGY
                                        ),
                                        Club(
                                                name = "Photography Club",
                                                description =
                                                        "Explore visual storytelling through portraits, street photography, and editing workshops.",
                                                category = ClubCategory.ARTS
                                        ),
                                        Club(
                                                name = "Hiking & Outdoors Club",
                                                description =
                                                        "Weekend trails, outdoor skill sessions, and nature escapes for all experience levels.",
                                                category = ClubCategory.SPORTS
                                        ),
                                        Club(
                                                name = "Film Society",
                                                description =
                                                        "Weekly screenings, director spotlights, and conversations about cinema from around the world.",
                                                category = ClubCategory.CULTURAL
                                        )
                                )
                        )
                val clubByName = clubs.associateBy { it.name }

                eventRepository.saveAll(
                        listOf(
                                Event(
                                        club = clubByName.getValue("Chess Club"),
                                        name = "Beginner's Chess Workshop",
                                        date = LocalDate.of(2026, 4, 8),
                                        location = "Library Seminar Room",
                                        type = typeByName.getValue("WORKSHOP"),
                                        owner = ownerAlice,
                                        description =
                                                "An introductory workshop covering chess rules, basic tactics, and opening principles."
                                ),
                                Event(
                                        club = clubByName.getValue("Chess Club"),
                                        name = "Spring Chess Tournament",
                                        date = LocalDate.of(2026, 4, 19),
                                        location = "Room A101",
                                        type = typeByName.getValue("COMPETITION"),
                                        owner = ownerAlice,
                                        description = "Rapid Swiss tournament open to all students."
                                ),
                                Event(
                                        club = clubByName.getValue("Chess Club"),
                                        name = "Chess Club Social Night",
                                        date = LocalDate.of(2026, 4, 24),
                                        location = "Student Lounge",
                                        type = typeByName.getValue("SOCIAL"),
                                        owner = ownerAlice,
                                        description =
                                                "Casual games and community time for club members and newcomers."
                                ),
                                Event(
                                        club = clubByName.getValue("Robotics Club"),
                                        name = "Line Follower Workshop",
                                        date = LocalDate.of(2026, 4, 12),
                                        location = "Engineering Lab 2",
                                        type = typeByName.getValue("WORKSHOP"),
                                        owner = ownerBob,
                                        description = "Build and tune a basic autonomous robot."
                                ),
                                Event(
                                        club = clubByName.getValue("Robotics Club"),
                                        name = "Embedded Systems Talk",
                                        date = LocalDate.of(2026, 4, 18),
                                        location = "Engineering Auditorium",
                                        type = typeByName.getValue("TALK"),
                                        owner = ownerBob,
                                        description =
                                                "Guest speaker session on real-time systems and robotics control loops."
                                ),
                                Event(
                                        club = clubByName.getValue("Robotics Club"),
                                        name = "Mini Robo Challenge",
                                        date = LocalDate.of(2026, 5, 2),
                                        location = "Innovation Hub",
                                        type = typeByName.getValue("COMPETITION"),
                                        owner = ownerBob,
                                        description =
                                                "Teams compete in obstacle and line-following rounds."
                                ),
                                Event(
                                        club = clubByName.getValue("Photography Club"),
                                        name = "Street Portrait Walk",
                                        date = LocalDate.of(2026, 4, 15),
                                        location = "City Center",
                                        type = typeByName.getValue("SOCIAL"),
                                        owner = ownerAlice,
                                        description =
                                                "Guided photo walk focused on portrait composition."
                                ),
                                Event(
                                        club = clubByName.getValue("Photography Club"),
                                        name = "Night Photography Workshop",
                                        date = LocalDate.of(2026, 4, 29),
                                        location = "Riverside",
                                        type = typeByName.getValue("WORKSHOP"),
                                        owner = ownerAlice,
                                        description =
                                                "Hands-on session on low-light settings, tripods, and long exposure."
                                ),
                                Event(
                                        club = clubByName.getValue("Photography Club"),
                                        name = "Editing Clinic",
                                        date = LocalDate.of(2026, 5, 6),
                                        location = "Media Lab",
                                        type = typeByName.getValue("MEETING"),
                                        owner = ownerAlice,
                                        description =
                                                "Bring your photos and get peer feedback on editing workflows."
                                ),
                                Event(
                                        club = clubByName.getValue("Hiking & Outdoors Club"),
                                        name = "Serra Sunrise Hike",
                                        date = LocalDate.of(2026, 4, 26),
                                        location = "North Trailhead",
                                        type = typeByName.getValue("MEETING"),
                                        owner = ownerBob,
                                        description =
                                                "Early morning hike with beginner-friendly pace."
                                ),
                                Event(
                                        club = clubByName.getValue("Hiking & Outdoors Club"),
                                        name = "Trail Safety Briefing",
                                        date = LocalDate.of(2026, 4, 10),
                                        location = "Sports Pavilion",
                                        type = typeByName.getValue("TALK"),
                                        owner = ownerBob,
                                        description =
                                                "Preparation session on safety, weather planning, and navigation basics."
                                ),
                                Event(
                                        club = clubByName.getValue("Hiking & Outdoors Club"),
                                        name = "Coastal Sunset Walk",
                                        date = LocalDate.of(2026, 5, 9),
                                        location = "Cliff Route",
                                        type = typeByName.getValue("SOCIAL"),
                                        owner = ownerBob,
                                        description =
                                                "Relaxed group walk with photo stops and picnic break."
                                ),
                                Event(
                                        club = clubByName.getValue("Film Society"),
                                        name = "Classic Noir Night",
                                        date = LocalDate.of(2026, 4, 22),
                                        location = "Auditorium B",
                                        type = typeByName.getValue("TALK"),
                                        owner = ownerCharlie,
                                        description =
                                                "Screening followed by discussion on noir cinema themes."
                                ),
                                Event(
                                        club = clubByName.getValue("Film Society"),
                                        name = "Short Film Showcase",
                                        date = LocalDate.of(2026, 5, 1),
                                        location = "Auditorium B",
                                        type = typeByName.getValue("OTHER"),
                                        owner = ownerCharlie,
                                        description =
                                                "Student-made short films and audience voting session."
                                ),
                                Event(
                                        club = clubByName.getValue("Film Society"),
                                        name = "Directors Roundtable",
                                        date = LocalDate.of(2026, 5, 13),
                                        location = "Humanities Hall",
                                        type = typeByName.getValue("MEETING"),
                                        owner = ownerCharlie,
                                        description =
                                                "Panel-style discussion on directing styles across film movements."
                                )
                        )
                )
        }

        private fun seedUsersIfMissing() {
                ensureUser("alice", "password123", AppRoleName.ROLE_EDITOR)
                ensureUser("bob", "password123", AppRoleName.ROLE_EDITOR)
                ensureUser("charlie", "password123", AppRoleName.ROLE_ADMIN)
        }

        private fun ensureUser(username: String, rawPassword: String, role: AppRoleName) {
                if (appUserRepository.existsByUsername(username)) {
                        return
                }

                val user =
                        AppUser(
                                username = username,
                                passwordHash = passwordEncoder.encode(rawPassword)
                        )
                user.setRoles(listOf(role))
                appUserRepository.save(user)
        }
}
