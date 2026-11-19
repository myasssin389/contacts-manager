package com.example.contacts_phase1.intents

import android.util.Patterns

/** The details we could find in text that another app shared with us. */
data class SharedContact(
    val name: String = "",
    val phone: String = "",
    val email: String = "",
)

/**
 * Reads a name, a phone number and an email address out of shared text, for example:
 *   Sara Ahmed
 *   +20 100 123 4567
 *   sara@example.com
 */
object SharedTextParser {

    fun parse(text: String): SharedContact {
        val email = Patterns.EMAIL_ADDRESS.matcher(text).let { matcher ->
            if (matcher.find()) matcher.group() else ""
        }
        val phone = Patterns.PHONE.matcher(text).let { matcher ->
            if (matcher.find()) matcher.group().trim() else ""
        }
        // The name is the first line that has no digits and no "@".
        val name = text.lineSequence()
            .map { line -> line.trim() }
            .firstOrNull { line -> line.isNotEmpty() && '@' !in line && line.none { it.isDigit() } }
            .orEmpty()

        return SharedContact(name = name.take(60), phone = phone, email = email)
    }
}
