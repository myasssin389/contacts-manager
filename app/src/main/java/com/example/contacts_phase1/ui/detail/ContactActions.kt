package com.example.contacts_phase1.ui.detail

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.contacts_phase1.R
import com.example.contacts_phase1.data.Contact
import com.example.contacts_phase1.intents.ContactIntents

/** The row of Call / SMS / Email / Map / Share buttons on the detail screen. Each one sends an implicit Intent. */
@Composable
fun ContactActions(contact: Contact, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier.fillMaxWidth(),
    ) {
        ActionButton(icon = Icons.Filled.Call, label = R.string.action_call) {
            ContactIntents.dial(context, contact.phone)
        }
        ActionButton(icon = Icons.AutoMirrored.Filled.Send, label = R.string.action_sms) {
            ContactIntents.sendSms(context, contact.phone)
        }
        ActionButton(icon = Icons.Filled.Email, label = R.string.action_email, enabled = contact.email.isNotBlank()) {
            ContactIntents.sendEmail(context, contact.email)
        }
        ActionButton(icon = Icons.Filled.Place, label = R.string.action_map, enabled = contact.city.isNotBlank()) {
            ContactIntents.showOnMap(context, contact.city)
        }
        ActionButton(icon = Icons.Filled.Share, label = R.string.action_share) {
            ContactIntents.share(context, contact)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActionButton(
    icon: ImageVector,
    @StringRes label: Int,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FilledTonalIconButton(onClick = onClick, enabled = enabled) {
            Icon(imageVector = icon, contentDescription = stringResource(label))
        }
        Text(text = stringResource(label), style = MaterialTheme.typography.labelMedium)
    }
}
