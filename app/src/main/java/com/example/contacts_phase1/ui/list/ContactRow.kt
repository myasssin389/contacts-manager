package com.example.contacts_phase1.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.contacts_phase1.R
import com.example.contacts_phase1.data.Contact
import com.example.contacts_phase1.ui.components.ContactPhoto

/** One compact line of the contact list: photo, name, phone and group, and a favorite heart. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactRow(
    contact: Contact,
    groupName: String?,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        headlineContent = {
            Text(text = contact.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        supportingContent = {
            Text(
                text = listOfNotNull(contact.phone, groupName).joinToString(" · "),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        leadingContent = {
            ContactPhoto(photoPath = contact.photoPath, name = contact.name, size = 48.dp)
        },
        trailingContent = {
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (contact.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = stringResource(
                        if (contact.isFavorite) R.string.remove_favorite else R.string.add_favorite
                    ),
                    tint = if (contact.isFavorite) MaterialTheme.colorScheme.tertiary else LocalContentColor.current,
                )
            }
        },
        modifier = modifier.clickable(onClick = onClick),
    )
}
