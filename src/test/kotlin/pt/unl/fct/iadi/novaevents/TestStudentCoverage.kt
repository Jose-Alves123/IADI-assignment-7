package pt.unl.fct.iadi.novaevents

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import pt.unl.fct.iadi.novaevents.service.EventFilter

class TestStudentCoverage {

    @Test
    fun eventFilterStartsEmpty() {
        val filter = EventFilter()

        assertNull(filter.type)
        assertNull(filter.clubId)
        assertNull(filter.from)
        assertNull(filter.to)
    }

    @Test
    fun eventFilterStoresValues() {
        val filter = EventFilter(type = "WORKSHOP", clubId = 1L)

        assertEquals("WORKSHOP", filter.type)
        assertEquals(1L, filter.clubId)
    }
}
