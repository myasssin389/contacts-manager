# contacts_phase1

A contacts app for Android, built with Kotlin, Jetpack Compose and Room for the FNI 421 Mobile Computing Phase 1 project.

## Features

- Add, edit and delete contacts, with name, phone, email, city, birthday, photo and group (Room, full CRUD)
- Groups (Family, Work, University) in a second table linked by a foreign key
- Search by name or phone, A–Z / Z–A sort (remembered with DataStore) and a favorites filter
- Call, SMS, email, show the city on a map and share a contact (implicit Intents)
- Share text from another app to contacts_phase1 to start a new contact (intent filter for ACTION_SEND)
- Material 3 theme with dark mode; layouts adapt to landscape and tablets

## How to run

1. Open the folder in Android Studio and let Gradle sync (it downloads Gradle 8.7 and the libraries the first time).
2. Run the `app` configuration on a phone or emulator with Android 8.0 (API 26) or newer.

## How it is built

| Part | Files | Course topic |
| --- | --- | --- |
| Screens | `MainActivity`, `DetailActivity`, `EditContactActivity` | Activities, explicit Intents with extras |
| UI | `ui/list`, `ui/detail`, `ui/edit`, `ui/components`, `ui/theme` | Jetpack Compose, state hoisting |
| Logic | `ui/ContactViewModel.kt` | ViewModel, StateFlow, coroutines |
| Data | `data/` | Room (Entity, DAO, Database), DataStore |
| Other apps | `intents/` | Implicit Intents, intent filters |
