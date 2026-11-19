package com.example.contacts_phase1.ui.edit

import android.util.Patterns
import androidx.annotation.StringRes
import com.example.contacts_phase1.R

/** One error message per field, as a string resource id; null means the field is fine. */
data class ContactErrors(
    @StringRes val name: Int? = null,
    @StringRes val phone: Int? = null,
    @StringRes val email: Int? = null,
) {
    val hasErrors: Boolean
        get() = name != null || phone != null || email != null
}

/**
 * Checks the form. It only returns the errors; the screen decides how to show them
 * (under each field), so there are no Toasts in here.
 */
object ContactValidator {

    fun validate(name: String, phone: String, email: String): ContactErrors = ContactErrors(
        name = if (name.isBlank()) R.string.error_name_required else null,
        phone = when {
            phone.isBlank() -> R.string.error_phone_required
            !Patterns.PHONE.matcher(phone.trim()).matches() -> R.string.error_phone_invalid
            else -> null
        },
        // Email is optional, but if one is typed it must look like an email address.
        email = if (email.isNotBlank() && !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            R.string.error_email_invalid
        } else {
            null
        },
    )
}
