package com.example.contacts_phase1.ui.edit

import androidx.annotation.StringRes
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.contacts_phase1.R
import com.example.contacts_phase1.data.Contact
import com.example.contacts_phase1.data.Group
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * The add/edit form. [initial] is an empty contact (id 0) when adding, or the saved one when editing.
 * [photoPicker] is a slot: EditContactActivity fills it with Person 6's PhotoPicker.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactEditScreen(
    initial: Contact,
    groups: List<Group>,
    onSave: (Contact) -> Unit,
    onCancel: () -> Unit,
    photoPicker: @Composable (photoPath: String?, name: String, onPhotoChange: (String) -> Unit) -> Unit,
) {
    // rememberSaveable keeps what the user typed when the screen rotates.
    var name by rememberSaveable { mutableStateOf(initial.name) }
    var phone by rememberSaveable { mutableStateOf(initial.phone) }
    var email by rememberSaveable { mutableStateOf(initial.email) }
    var city by rememberSaveable { mutableStateOf(initial.city) }
    var birthdayEpochDay by rememberSaveable { mutableStateOf(initial.birthday?.toEpochDay()) }
    var photoPath by rememberSaveable { mutableStateOf(initial.photoPath) }
    var groupId by rememberSaveable { mutableStateOf(initial.groupId) }
    // Errors appear only after the first tap on Save, not while the user is still typing.
    var showErrors by rememberSaveable { mutableStateOf(false) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    // true after a valid Save tap, while the screen is closing.
    var isSaving by remember { mutableStateOf(false) }

    val errors = ContactValidator.validate(name, phone, email)
    val birthday = birthdayEpochDay?.let { day -> LocalDate.ofEpochDay(day) }

    fun save() {
        // A second quick tap would save a new contact twice.
        if (isSaving) return
        showErrors = true
        if (errors.hasErrors) return
        isSaving = true
        onSave(
            initial.copy(
                name = name.trim(),
                phone = phone.trim(),
                email = email.trim(),
                city = city.trim(),
                birthday = birthday,
                photoPath = photoPath,
                groupId = groupId,
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(if (initial.id == 0L) R.string.new_contact else R.string.edit_contact))
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.cancel))
                    }
                },
                actions = {
                    TextButton(onClick = { save() }, enabled = !isSaving) {
                        Text(stringResource(R.string.save))
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                // widthIn keeps the form readable on tablets and in landscape.
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            ) {
                photoPicker(photoPath, name) { newPath -> photoPath = newPath }

                ContactField(
                    value = name,
                    onValueChange = { name = it },
                    label = R.string.label_name,
                    error = errors.name.takeIf { showErrors },
                    capitalization = KeyboardCapitalization.Words,
                )
                ContactField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = R.string.label_phone,
                    error = errors.phone.takeIf { showErrors },
                    keyboardType = KeyboardType.Phone,
                )
                ContactField(
                    value = email,
                    onValueChange = { email = it },
                    label = R.string.label_email,
                    error = errors.email.takeIf { showErrors },
                    keyboardType = KeyboardType.Email,
                )
                ContactField(
                    value = city,
                    onValueChange = { city = it },
                    label = R.string.label_city,
                    error = null,
                    capitalization = KeyboardCapitalization.Words,
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    OutlinedButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Filled.DateRange, contentDescription = null)
                        Text(
                            text = birthday?.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
                                ?: stringResource(R.string.choose_birthday),
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                    if (birthday != null) {
                        TextButton(onClick = { birthdayEpochDay = null }) {
                            Text(stringResource(R.string.clear))
                        }
                    }
                }

                Text(
                    text = stringResource(R.string.label_group),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                ) {
                    FilterChip(
                        selected = groupId == null,
                        onClick = { groupId = null },
                        label = { Text(stringResource(R.string.no_group)) },
                    )
                    groups.forEach { group ->
                        FilterChip(
                            selected = groupId == group.id,
                            onClick = { groupId = group.id },
                            label = { Text(group.name) },
                        )
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        // The Material 3 date picker works in UTC milliseconds; we store the day as a LocalDate.
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = birthday?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli(),
            selectableDates = PastDatesOnly,
        )
        // Today's ring is only a guide: once a date is picked it goes away,
        // so the picked date is the only one that stands out.
        val nothingPicked = pickerState.selectedDateMillis == null
        val pickerColors = DatePickerDefaults.colors(
            todayDateBorderColor = if (nothingPicked) MaterialTheme.colorScheme.primary else Color.Transparent,
            todayContentColor = if (nothingPicked) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let { millis ->
                            birthdayEpochDay = Instant.ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                                .toEpochDay()
                        }
                        showDatePicker = false
                    },
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            },
            colors = pickerColors,
        ) {
            DatePicker(state = pickerState, colors = pickerColors)
        }
    }
}

/** A birthday cannot be in the future, so the picker greys out the days after today. */
@OptIn(ExperimentalMaterial3Api::class)
private object PastDatesOnly : SelectableDates {
    // The picker gives each day as midnight UTC in milliseconds, so today is compared the same way.
    override fun isSelectableDate(utcTimeMillis: Long): Boolean =
        utcTimeMillis <= LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    override fun isSelectableYear(year: Int): Boolean = year <= LocalDate.now().year
}

/** A text field with its label, and an error message underneath when [error] is not null. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactField(
    value: String,
    onValueChange: (String) -> Unit,
    @StringRes label: Int,
    @StringRes error: Int?,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(label)) },
        isError = error != null,
        supportingText = if (error != null) {
            { Text(stringResource(error)) }
        } else {
            null
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            capitalization = capitalization,
            keyboardType = keyboardType,
            imeAction = ImeAction.Next,
        ),
        modifier = Modifier.fillMaxWidth(),
    )
}
