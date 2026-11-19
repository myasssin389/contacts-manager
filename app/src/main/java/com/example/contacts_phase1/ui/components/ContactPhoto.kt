package com.example.contacts_phase1.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.contacts_phase1.R
import java.io.File

/**
 * A round contact photo. Without a photo it shows the first letter of the name instead.
 * Used by the list, the detail screen and the photo picker.
 */
@Composable
fun ContactPhoto(
    photoPath: String?,
    name: String,
    size: Dp,
    modifier: Modifier = Modifier,
) {
    if (photoPath != null) {
        AsyncImage(
            model = File(photoPath),
            contentDescription = stringResource(R.string.contact_photo, name),
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size)
                .clip(CircleShape),
        )
    } else {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
        ) {
            Text(
                text = name.trim().take(1).uppercase().ifEmpty { "?" },
                style = if (size >= 64.dp) {
                    MaterialTheme.typography.headlineMedium
                } else {
                    MaterialTheme.typography.titleMedium
                },
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}
