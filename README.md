# Student Profile App

A small standalone Android app for capturing and reviewing a student profile
(name, current education level, area of interest), built as a practical
Android development assessment. It is intentionally separate from any larger
app/repo.

## Prerequisites

- **Android Studio** (Ladybug/2024.2 or newer recommended), which bundles a
  compatible JDK, so you generally don't need to install Java separately.
- An **Android emulator** (via Android Studio's Device Manager) or a
  **physical Android phone** with USB debugging enabled, running **Android
  8.0 (API 26)** or newer.
- Internet access on first open, so Gradle can download dependencies.

## Setup / build / run

1. Clone the repository:
   ```
   git clone <your-repo-url>
   cd StudentProfileApp
   ```
2. Open the project folder in Android Studio (**File → Open**).
3. Let Gradle sync finish (first sync downloads dependencies and can take a
   few minutes).
4. Select a device (an emulator you've created, or a physical phone plugged
   in with USB debugging on) from the device dropdown.
5. Click **Run ▶** (or `Shift+F10`).

To build from the command line instead (after step 2's SDK is configured,
e.g. once Android Studio has written `local.properties` for you):
```
./gradlew assembleDebug        # builds app/build/outputs/apk/debug/app-debug.apk
./gradlew testDebugUnitTest    # runs JVM unit tests
./gradlew connectedAndroidTest # runs on-device UI tests (needs an emulator/device attached)
```

## Implementation overview

### UI approach: Jetpack Compose

The UI is built with **Jetpack Compose** rather than XML layouts. Compose
was chosen because:
- It's the current recommended toolkit for new Android UI, and the one I
  wanted to be comfortable with going forward.
- For a form this size (a handful of fields plus a summary screen), Compose
  needed noticeably less boilerplate than XML + a separate Kotlin
  file per screen (no `findViewById`/ViewBinding wiring).
- State handling is explicit: the whole screen is a pure function of
  `ProfileUiState`, which makes it easy to explain and to unit-test the
  validation logic in isolation from any UI.

### Structure

```
app/src/main/java/com/tejasnarendra/studentprofile/
├── MainActivity.kt              # hosts the ViewModel, switches between the two screens
├── ProfileViewModel.kt          # UI state + save/edit/validate logic
├── data/
│   ├── StudentProfile.kt        # data class + EducationLevel/AreaOfInterest enums
│   ├── ProfileValidation.kt     # pure validation logic (unit tested)
│   └── ProfileRepository.kt     # reads/writes the profile via DataStore
└── ui/
    ├── ProfileFormScreen.kt     # the form (name, dropdowns, Save)
    ├── ProfileSummaryScreen.kt  # the saved-profile confirmation screen
    └── theme/                   # Material3 theme (colors/typography)
```

There is no navigation library: with only two screens and one simple rule
for switching between them, a `Screen` enum (`FORM` / `SUMMARY`) held in the
ViewModel and a `when` in `MainActivity` was simpler than pulling in
Navigation Compose for something this small.

### Persistence: Jetpack DataStore (Preferences)

The saved profile is stored with **Jetpack DataStore (Preferences DataStore)**,
in `ProfileRepository`. Reasoning:
- There is exactly **one** small, flat record to persist (three fields), not
  a collection of rows, so a full database (Room/SQLite) would add
  schema/DAO/migration machinery this task doesn't need.
- Plain `SharedPreferences` could also work, but its API is
  synchronous/callback-based; DataStore is coroutine/`Flow`-based, safer to
  use off the main thread, and is what Google now recommends over
  SharedPreferences for new code.
- It persists across process death and app restarts, satisfying "saved
  profile information must remain available after the app is closed and
  reopened."

### Validation

`ProfileValidation.validateName` is a small, pure function kept out of the
ViewModel and UI specifically so it can be unit tested on the JVM without an
emulator (see `ProfileValidationTest`). The name field is trimmed before the
empty check, so whitespace-only input is also rejected.

### Orientation / configuration changes

The manifest does **not** suppress configuration changes (no
`android:configChanges` override on `MainActivity`), so on rotation the
Activity is destroyed and recreated the normal Android way. This works
correctly here because all UI state (typed name, dropdown selections,
current screen, saved profile) lives in `ProfileViewModel`, and ViewModels
are retained across configuration changes by the Android framework, so
nothing typed into the form is lost on rotation. This was a deliberate
choice over `configChanges` because it's the idiomatic pattern and better
demonstrates the actual lifecycle behaviour being handled correctly, rather
than sidestepped.

### Keyboard handling

The form screen wraps its content in `Modifier.verticalScroll(...)` plus
`Modifier.imePadding()`, so when the keyboard opens, the content scrolls and
the Save button stays reachable instead of being permanently hidden behind
the keyboard.

### Rapid Save taps

`ProfileViewModel.saveProfile()` checks `uiState.isSaving` and ignores
re-entrant calls while a save is already in flight, and the Save button is
disabled (`enabled = !isSaving`) during that window, so rapid tapping
cannot fire multiple saves or navigate twice.

### Accessibility

- All text uses Material3 typography in `sp`, so it scales with the
  system's font-size accessibility setting rather than staying a fixed
  pixel size.
- Labels are attached to every field (`OutlinedTextField`'s `label`), and
  the Save/Edit buttons use plain, descriptive text rather than icons only.
- Material3 `Button`/`OutlinedTextField` components meet Android's minimum
  48dp touch-target guidance by default; no custom small tap targets were
  introduced.

## Testing

See [`docs/TEST_REPORT.md`](docs/TEST_REPORT.md) for the full functional +
non-functional test report (expected/actual/pass-fail/evidence).

Automated tests included in the repo:
- `app/src/test/.../ProfileValidationTest.kt`: JVM unit tests for the name
  validation rule (empty, whitespace-only, valid, valid-with-padding).
- `app/src/androidTest/.../ProfileFlowTest.kt`: on-device Compose UI tests
  covering the empty-name error, a valid save reaching the summary screen,
  and Edit Profile returning to the form with the saved values pre-filled.

Run them with:
```
./gradlew testDebugUnitTest
./gradlew connectedAndroidTest   # requires an emulator/device to be running
```

## Known issues / limitations

- Dark theme is implemented (`Theme.kt` follows the system's light/dark
  setting via `isSystemInDarkTheme()`) but was not manually verified during
  testing. Only light mode was checked on-device.
- There's no way to delete a saved profile once created, only edit/overwrite
  it, which is acceptable for this assignment's scope (a single-profile app) but
  would need addressing for a multi-profile version.
- During testing, running `adb shell screenrecord` at the same time as
  scripted input automation reliably triggered a "isn't responding" dialog
  on the development machine (reproduced twice, recovered cleanly both
  times). This was host CPU contention between the video encoder and input
  automation, not an app defect. It didn't occur during any other testing
  (rapid taps, rotation, edit flow, small-screen run) when screen recording
  wasn't also running at the same time. See `docs/TEST_REPORT.md` for
  details.

## What I learned / where AI helped

- Everything in this project was new to me. This was my first time writing
  Kotlin, using Jetpack Compose, and building an Android app at all.
- I personally reviewed the DataStore repository code (`ProfileRepository.kt`)
  and worked through how the Flow-based read works: the saved profile is
  exposed as a reactive stream rather than read with a one-off blocking call.
- What tripped me up was testing the text-scaling (font size) accessibility
  behavior: changing the system font size via
  `adb shell settings put system font_scale` didn't visibly change the app's
  text until the app was force-stopped and relaunched. I had to think through
  why: a config change applied this way isn't pushed live to an
  already-running activity the same way toggling the setting in the Settings
  app is, so the app only picks up the new scale on its next launch. That's a
  quirk of testing this way via adb, not a problem with the app itself.
- **AI disclosure:** This project was scaffolded with OpenAI's assistance
  (project structure, Gradle/toolchain setup, and an initial implementation
  of the screens/ViewModel/repository/tests). Beyond reviewing the
  persistence code myself as above, I went through the rest of the code
  file-by-file with OpenAI to be able to explain it, and the manual test pass
  (edit flow, orientation, rapid taps, keyboard, small-screen run,
  accessibility/font-scaling, screen recording) was done with manuelly
  via the emulator and reviewed each result.
