package com.example.contacts_phase1.ui.components

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.contacts_phase1.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.UUID

/**
 * The contact photo with an "Add photo" button. Tapping either opens the system photo picker
 * (an Intent under the hood). The chosen picture is copied into the app's own storage,
 * because the picker's Uri stops working after the app restarts.
 */
@Composable
fun PhotoPicker(
    photoPath: String?,
    name: String,
    onPhotoPicked: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            scope.launch {
                // Copying a file is slow work, so it runs off the main thread.
                val savedPath = withContext(Dispatchers.IO) { copyToAppStorage(context, uri) }
                if (savedPath != null) {
                    onPhotoPicked(savedPath)
                } else {
                    Toast.makeText(context, R.string.photo_failed, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    val pickPhoto = {
        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        ContactPhoto(
            photoPath = photoPath,
            name = name,
            size = 120.dp,
            modifier = Modifier.clickable(onClick = pickPhoto),
        )
        TextButton(onClick = pickPhoto) {
            Text(stringResource(if (photoPath == null) R.string.add_photo else R.string.change_photo))
        }
    }
}

/** Copies the picked image to files/photos/ and returns its path, or null if it fails. */
private fun copyToAppStorage(context: Context, uri: Uri): String? {
    val folder = File(context.filesDir, "photos").apply { mkdirs() }
    val file = File(folder, "${UUID.randomUUID()}.jpg")
    return try {
        val input = context.contentResolver.openInputStream(uri) ?: return null
        input.use { source -> file.outputStream().use { target -> source.copyTo(target) } }
        file.absolutePath
    } catch (e: IOException) {
        null
    }
}
