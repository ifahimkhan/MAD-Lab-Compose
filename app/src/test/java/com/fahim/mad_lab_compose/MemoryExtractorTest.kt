package com.fahim.mad_lab_compose

import com.fahim.mad_lab_compose.data.memory.MemoryExtractor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MemoryExtractorTest {

    @Test
    fun testBasicMessageExtractsNoMemory() {
        val memories = MemoryExtractor.extractMemories("Hello, how are you?")
        assertTrue(memories.isEmpty())
    }

    @Test
    fun testNameExtraction() {
        val memories = MemoryExtractor.extractMemories("My name is Charmy.")
        assertEquals(1, memories.size)
        assertEquals("Name", memories[0].key)
        assertEquals("Charmy", memories[0].value)

        val memories2 = MemoryExtractor.extractMemories("Call me Charmy")
        assertEquals(1, memories2.size)
        assertEquals("Name", memories2[0].key)
        assertEquals("Charmy", memories2[0].value)
    }

    @Test
    fun testCombinedCollegeAndCourseExtraction() {
        val memories = MemoryExtractor.extractMemories("I study Computer Engineering at NMIMS.")
        assertEquals(2, memories.size)
        assertEquals("Course", memories[0].key)
        assertEquals("Computer Engineering", memories[0].value)
        assertEquals("College", memories[1].key)
        assertEquals("NMIMS", memories[1].value)
    }

    @Test
    fun testFavouriteSubjectExtraction() {
        val memories = MemoryExtractor.extractMemories("My favourite subject is Operating Systems.")
        assertEquals(1, memories.size)
        assertEquals("Favourite Subject", memories[0].key)
        assertEquals("Operating Systems", memories[0].value)

        val memories2 = MemoryExtractor.extractMemories("My favourite subject is Computer Networks.")
        assertEquals(1, memories2.size)
        assertEquals("Favourite Subject", memories2[0].key)
        assertEquals("Computer Networks", memories2[0].value)
    }

    @Test
    fun testGoalExtraction() {
        val memories = MemoryExtractor.extractMemories("My goal is to improve programming skills.")
        assertEquals(1, memories.size)
        assertEquals("Goal", memories[0].key)
        assertEquals("to improve programming skills", memories[0].value)
    }

    @Test
    fun testLocationAndAgeExtraction() {
        val locMemories = MemoryExtractor.extractMemories("I live in Mumbai")
        assertEquals(1, locMemories.size)
        assertEquals("Location", locMemories[0].key)
        assertEquals("Mumbai", locMemories[0].value)

        val ageMemories = MemoryExtractor.extractMemories("I am 20 years old")
        assertEquals(1, ageMemories.size)
        assertEquals("Age", ageMemories[0].key)
        assertEquals("20 years", ageMemories[0].value)
    }
}
