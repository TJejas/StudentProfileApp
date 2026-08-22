# Test Report — Student Profile App

> Fill in the **Actual result**, **Status**, and **Notes/evidence** columns after each manual run.
> "Device/emulator" should be a real name (e.g. "Pixel 6 API 34 emulator", "Pixel 7 physical, Android 14").

## Environment used

| Item | Value |
|---|---|
| Android Studio version | _fill in (Settings → About)_ |
| Primary device/emulator | StudentProfileEmulator — Pixel 6, API 34 |
| Android version (primary) | Android 14 ("UpsideDownCake"), google_apis x86_64 |
| Second device/emulator | _fill in — create a second AVD with a different screen size_ |
| Android version (second) | _fill in_ |

## Functional tests

| # | Test | Expected result | Actual result | Status | Notes / evidence |
|---|---|---|---|---|---|
| 1 | Application launch | App starts without crashing; profile screen is displayed. | App launched and rendered the form (Name, Current Education Level = Class 10, Area of Interest = Engineering, Save Profile) with no crash. | **Pass** | `docs/screenshots/form.png` |
| 2 | Empty name | Clear validation message shown; profile not saved. | Tapping Save with an empty name kept the user on the form, outlined the Name field in red, and showed "Name cannot be empty" below it. | **Pass** | `docs/screenshots/validation_error.png`. Also covered by automated test `ProfileFlowTest.savingWithEmptyName_showsValidationError_andDoesNotNavigate` and unit test `ProfileValidationTest`. |
| 3 | Valid profile | Entered values saved; profile summary displayed. | Entering a name and tapping Save showed "Saving..." briefly, then navigated to "Profile Saved" showing the entered Name/Education Level/Area of Interest. | **Pass** | `docs/screenshots/summary.png`. Also covered by `ProfileFlowTest.savingWithValidName_showsSummaryScreen`. |
| 4 | Edit profile | Existing values available and can be updated. | _fill in — tap Edit Profile on the summary screen and confirm the form reopens pre-filled._ | Pass/Fail | Covered by `ProfileFlowTest.editProfile_returnsToFormWithSavedValues`. |
| 5 | Persistence | Saved info still available after closing and reopening the app. | Force-stopped the app (`adb shell am force-stop`) after saving, then relaunched — it opened directly to the "Profile Saved" summary screen with the same values. | **Pass** | Verified via adb on the emulator above. |
| 6 | Small screen | Content remains readable/usable, no broken layout. | _fill in — run on a small-screen AVD (e.g. Pixel 4a / "Small Phone")._ | Pass/Fail | |
| 7 | Second screen size | App remains usable on another device configuration. | _fill in — run on a tablet or large-phone AVD._ | Pass/Fail | |
| 8 | Orientation change | No crash; state/behaviour acceptable, or limitation documented. | _fill in — rotate (Ctrl+F11/F12 in the emulator) on both the form (mid-typing) and the summary screen._ | Pass/Fail | State should survive: see README's "Orientation / configuration changes" section for why. |
| 9 | Rapid Save taps | No crash or unintended duplicate navigation. | _fill in — tap Save several times quickly with a valid name._ | Pass/Fail | The Save button disables itself (`isSaving`) while a save is in flight — see `ProfileViewModel.saveProfile()`. |
| 10 | Keyboard | Fields and primary action remain accessible while entering text. | _fill in — tap the Name field and confirm Save is still reachable (scroll if needed) while the keyboard is open._ | Pass/Fail | The form scrolls and applies `imePadding()` so Save stays reachable above the keyboard. |

## Non-functional test

**What was tested:** _fill in, e.g. "startup responsiveness" or "text scaling accessibility"._

**How it was tested:** _fill in, e.g. steps taken, tool used (Logcat timings, system Font size setting, etc.)._

**What was observed:** _fill in the actual result._

## Known issues / limitations

- During initial setup, the emulator briefly showed a "System UI isn't responding" ANR dialog while several large SDK/tooling downloads were running at the same time on the host machine. It cleared on its own once those finished and did not recur; it was host CPU contention, not an app issue (the app's own UI was unaffected underneath the dialog in the screenshot taken at the time).
- _fill in anything else discovered during your own testing that wasn't fixed, and why._

## Screenshots / recording

- Main form screen: `docs/screenshots/form.png`
- Saved profile screen: `docs/screenshots/summary.png`
- Screen recording: `docs/recording.mp4` (or a link if too large for the repo)
