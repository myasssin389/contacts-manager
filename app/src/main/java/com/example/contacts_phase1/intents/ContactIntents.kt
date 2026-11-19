package com.example.contacts_phase1.intents

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.contacts_phase1.R
import com.example.contacts_phase1.data.Contact

/**
 * Implicit Intents: we say WHAT should happen (dial, send, view) and Android chooses
 * the app that can do it. The URI schemes tel:, smsto:, mailto: and geo: are from Lecture 3.
 */
object ContactIntents {

    /** Opens the dialer with the number filled in (ACTION_DIAL needs no permission; the user presses Call). */
    fun dial(context: Context, phone: String) {
        start(context, Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null)))
    }

    fun sendSms(context: Context, phone: String) {
        start(context, Intent(Intent.ACTION_SENDTO, Uri.fromParts("smsto", phone, null)))
    }

    fun sendEmail(context: Context, email: String) {
        start(context, Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null)))
    }

    /** Opens a map app searching for the place, like geo:0,0?q=coffee in Lecture 3. */
    fun showOnMap(context: Context, place: String) {
        start(context, Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + Uri.encode(place))))
    }

    /** Sends the contact as plain text; the chooser lets the user pick WhatsApp, Gmail, and so on. */
    fun share(context: Context, contact: Contact) {
        val text = buildString {
            appendLine(contact.name)
            appendLine(context.getString(R.string.share_phone, contact.phone))
            if (contact.email.isNotBlank()) appendLine(context.getString(R.string.share_email, contact.email))
            if (contact.city.isNotBlank()) appendLine(context.getString(R.string.share_city, contact.city))
        }.trim()

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        start(context, Intent.createChooser(sendIntent, context.getString(R.string.share_chooser_title)))
    }

    /** Starts the Intent, or explains that no installed app can handle it. */
    private fun start(context: Context, intent: Intent) {
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, R.string.no_app_found, Toast.LENGTH_SHORT).show()
        }
    }
}
