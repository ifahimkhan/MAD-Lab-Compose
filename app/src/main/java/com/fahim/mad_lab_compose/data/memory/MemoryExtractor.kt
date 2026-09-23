package com.fahim.mad_lab_compose.data.memory

/**
 * Extracted memory item with normalized key and sanitized value.
 */
data class ExtractedMemory(
    val key: String,
    val value: String
)

object MemoryExtractor {

    /**
     * Analyzes a user message and extracts structured facts that are worth remembering.
     * Returns an empty list if no memorable facts are present.
     */
    fun extractMemories(message: String): List<ExtractedMemory> {
        val trimmed = message.trim().trimEnd('.', '!', '?', ',')
        val memories = mutableListOf<ExtractedMemory>()

        // 1. Combined College & Course: e.g. "I study Computer Engineering at NMIMS"
        val studyAtRegex = Regex(
            """(?i)^(?:i\s+study|i\s+am\s+studying)\s+(.+?)\s+at\s+(.+)$"""
        )
        studyAtRegex.find(trimmed)?.let { match ->
            val course = cleanValue(match.groupValues[1])
            val college = cleanValue(match.groupValues[2])
            if (course.isNotBlank()) memories.add(ExtractedMemory("Course", course))
            if (college.isNotBlank()) memories.add(ExtractedMemory("College", college))
            return memories
        }

        // 2. Name: e.g. "My name is Charmy", "I am Charmy", "Call me Charmy", "You can call me Charmy"
        val namePatterns = listOf(
            Regex("""(?i)^(?:hi,?\s*|hello,?\s*)?my\s+name\s+is\s+([A-Za-z\s.'-]+)$"""),
            Regex("""(?i)^(?:you\s+can\s+)?call\s+me\s+([A-Za-z\s.'-]+)$"""),
            Regex("""(?i)^i\s+am\s+([A-Z][a-zA-Z]+)(?:\s+[A-Z][a-zA-Z]+)*$""")
        )
        for (pattern in namePatterns) {
            pattern.find(trimmed)?.let { match ->
                val name = cleanValue(match.groupValues[1])
                // Filter out non-names like "I am happy", "I am tired", etc.
                val lower = name.lowercase()
                val nonNames = setOf(
                    "happy", "sad", "tired", "busy", "fine", "good", "great", "ok", "okay",
                    "here", "ready", "thinking", "learning", "studying", "working", "trying",
                    "student", "developer", "engineer"
                )
                if (!nonNames.contains(lower) && name.isNotBlank() && name.length in 2..50) {
                    memories.add(ExtractedMemory("Name", capitalizeWords(name)))
                    return memories
                }
            }
        }

        // 3. College / University: e.g. "My college is NMIMS", "My university is Harvard", "I go to NMIMS"
        val collegePatterns = listOf(
            Regex("""(?i)^my\s+(?:college|university|school|institute)\s+is\s+(.+)$"""),
            Regex("""(?i)^(?:i\s+study\s+at|i\s+go\s+to|i\s+am\s+in)\s+(.+?)(?:\s+college|\s+university|\s+institute)?$"""),
            Regex("""(?i)^college:\s*(.+)$""")
        )
        for (pattern in collegePatterns) {
            pattern.find(trimmed)?.let { match ->
                val college = cleanValue(match.groupValues[1])
                if (college.isNotBlank()) {
                    memories.add(ExtractedMemory("College", college))
                    return memories
                }
            }
        }

        // 4. Course / Major / Branch: e.g. "My course is Computer Engineering", "My major is CS", "I am studying CS"
        val coursePatterns = listOf(
            Regex("""(?i)^my\s+(?:course|major|branch|degree|stream)\s+is\s+(.+)$"""),
            Regex("""(?i)^(?:i\s+study|i\s+am\s+studying)\s+(.+)$"""),
            Regex("""(?i)^course:\s*(.+)$""")
        )
        for (pattern in coursePatterns) {
            pattern.find(trimmed)?.let { match ->
                val course = cleanValue(match.groupValues[1])
                if (course.isNotBlank()) {
                    memories.add(ExtractedMemory("Course", course))
                    return memories
                }
            }
        }

        // 5. Favourite / Favorite items: e.g. "My favourite subject is Operating Systems", "My favorite food is Pizza"
        val favRegex = Regex(
            """(?i)^my\s+(?:fav|favourite|favorite)\s+([a-zA-Z\s]+?)\s+is\s+(.+)$"""
        )
        favRegex.find(trimmed)?.let { match ->
            val itemType = capitalizeWords(cleanValue(match.groupValues[1]))
            val itemValue = cleanValue(match.groupValues[2])
            if (itemType.isNotBlank() && itemValue.isNotBlank()) {
                memories.add(ExtractedMemory("Favourite $itemType", itemValue))
                return memories
            }
        }

        // 6. Goals: e.g. "My goal is to improve programming skills", "I want to become an Android Developer", "I aim to..."
        val goalPatterns = listOf(
            Regex("""(?i)^my\s+(?:goal|aim|dream|target|ambition)\s+is\s+(.+)$"""),
            Regex("""(?i)^(?:i\s+want\s+to|i\s+aim\s+to|i\s+aspire\s+to)\s+(.+)$"""),
            Regex("""(?i)^goal:\s*(.+)$""")
        )
        for (pattern in goalPatterns) {
            pattern.find(trimmed)?.let { match ->
                val goal = cleanValue(match.groupValues[1])
                if (goal.isNotBlank()) {
                    memories.add(ExtractedMemory("Goal", goal))
                    return memories
                }
            }
        }

        // 7. General Preferences & Likes: e.g. "I like Python", "I love Coding", "My hobby is Gaming"
        val preferencePatterns = listOf(
            Regex("""(?i)^my\s+hobby\s+is\s+(.+)$"""),
            Regex("""(?i)^(?:i\s+really\s+like|i\s+like|i\s+love|i\s+prefer)\s+(.+)$""")
        )
        for (pattern in preferencePatterns) {
            pattern.find(trimmed)?.let { match ->
                val pref = cleanValue(match.groupValues[1])
                if (pref.isNotBlank() && pref.length <= 80) {
                    memories.add(ExtractedMemory("Preference", pref))
                    return memories
                }
            }
        }

        // 8. Location / City: e.g. "I live in Mumbai", "My city is Mumbai", "I am from Mumbai"
        val locationPatterns = listOf(
            Regex("""(?i)^(?:i\s+live\s+in|i\s+am\s+from|i\s+stay\s+in)\s+([A-Za-z\s,.-]+)$"""),
            Regex("""(?i)^my\s+(?:city|home\s*town|location)\s+is\s+([A-Za-z\s,.-]+)$""")
        )
        for (pattern in locationPatterns) {
            pattern.find(trimmed)?.let { match ->
                val loc = cleanValue(match.groupValues[1])
                if (loc.isNotBlank() && loc.length <= 60) {
                    memories.add(ExtractedMemory("Location", capitalizeWords(loc)))
                    return memories
                }
            }
        }

        // 9. Age: e.g. "I am 20 years old", "My age is 20"
        val agePatterns = listOf(
            Regex("""(?i)^i\s+am\s+(\d{1,3})\s+years?\s+old$"""),
            Regex("""(?i)^my\s+age\s+is\s+(\d{1,3})$""")
        )
        for (pattern in agePatterns) {
            pattern.find(trimmed)?.let { match ->
                val age = cleanValue(match.groupValues[1])
                if (age.isNotBlank()) {
                    memories.add(ExtractedMemory("Age", "$age years"))
                    return memories
                }
            }
        }

        // 10. Profession / Role: e.g. "I work as a Software Engineer", "My job is Data Analyst"
        val jobPatterns = listOf(
            Regex("""(?i)^(?:i\s+work\s+as\s+an?|i\s+am\s+a|i\s+am\s+an)\s+([a-zA-Z\s]+)$"""),
            Regex("""(?i)^my\s+(?:job|profession|role|title)\s+is\s+([a-zA-Z\s]+)$""")
        )
        for (pattern in jobPatterns) {
            pattern.find(trimmed)?.let { match ->
                val job = cleanValue(match.groupValues[1])
                val lower = job.lowercase()
                val ignored = setOf("student", "boy", "girl", "person", "human")
                if (!ignored.contains(lower) && job.isNotBlank() && job.length <= 60) {
                    memories.add(ExtractedMemory("Profession", capitalizeWords(job)))
                    return memories
                }
            }
        }

        return memories
    }

    private fun cleanValue(text: String): String {
        return text.trim()
            .trimStart(':', '-', '=', ' ')
            .trimEnd('.', '!', '?', ',', ';')
            .trim()
    }

    private fun capitalizeWords(text: String): String {
        return text.split(" ")
            .filter { it.isNotBlank() }
            .joinToString(" ") { word ->
                word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }
    }
}
