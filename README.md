# contacts_phase1

A contacts app for Android, built with Kotlin, Jetpack Compose and Room for the FNI 421 Mobile Computing Phase 1 project.

## Features

- Add, edit and delete contacts, with name, phone, email, city, birthday, photo and group (Room, full CRUD)
- Groups (Family, Work, University, Other) in a second table linked by a foreign key; a Room migration adds "Other" on phones that had version 1
- Search by name or phone, with the last five searches shown under the empty search box
- A–Z / Z–A sort and recent searches remembered with DataStore, and a favorites filter
- Call, SMS, email, show the city on a map and share a contact (implicit Intents)
- Share text from another app to contacts_phase1 to start a new contact (intent filter for ACTION_SEND)
- Material 3 theme with dark mode; layouts adapt to landscape and tablets

## Abstract

The two-page abstract in ACM format is in [`docs/abstract.pdf`](docs/abstract.pdf). Its LaTeX source is
`docs/abstract.tex` with `docs/references.bib`; open both in Overleaf (ACM template) to edit and rebuild it.

## Fixes after testing on a phone

- The search box now lets go of the focus when you tap elsewhere, scroll, press Search or close the keyboard
- The favorites filter heart is grey when off and colored when on
- The date picker stops highlighting today once another date is picked, and future birthdays are blocked
- Tapping Save twice no longer saves a contact twice, and a double tap no longer opens a screen twice
- Old photo files are deleted when a photo is replaced or a contact is deleted
- The list no longer flashes "No contacts yet" at start-up, and dark mode no longer flashes white

## How to run

1. Open the folder in Android Studio and let Gradle sync (it downloads Gradle 8.7 and the libraries the first time).
2. Run the `app` configuration on a phone or emulator with Android 8.0 (API 26) or newer.

## How it is built

| Part | Files | Course topic |
| --- | --- | --- |
| Screens | `MainActivity`, `DetailActivity`, `EditContactActivity` | Activities, explicit Intents with extras |
| UI | `ui/list`, `ui/detail`, `ui/edit`, `ui/components`, `ui/theme` | Jetpack Compose, state hoisting |
| Logic | `ui/ContactViewModel.kt` | ViewModel, StateFlow, coroutines |
| Data | `data/` | Room (Entity, DAO, Database, migration), DataStore |
| Other apps | `intents/` | Implicit Intents, intent filters |

## Team

| Person | Area |
| --- | --- |
| Mohamed Yassin | Project setup, manifest, activities, README |
| Mohammed El Shaarawy | Room database |
| Mohamed Mahrous | Repository, ViewModel, settings and the abstract |
| Mohamed Saber | Theme and contact list |
| Ahmed Nabil | Detail and edit screens |
| Ziad Magdy | Intents to other apps and the photo picker |
