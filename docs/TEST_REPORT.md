# Test Report — Student Profile App

> Fill in the **Actual result**, **Status**, and **Notes/evidence** columns after each manual run.
> "Device/emulator" should be a real name (e.g. "Pixel 6 API 34 emulator", "Pixel 7 physical, Android 14").

## Environment used

| Item | Value |
|---|---|
| Android Studio version | _fill in_ |
| Primary device/emulator | _fill in_ |
| Android version (primary) | _fill in_ |
| Second device/emulator | _fill in_ |
| Android version (second) | _fill in_ |

## Functional tests

| # | Test | Expected result | Actual result | Status | Notes / evidence |
|---|---|---|---|---|---|
| 1 | Application launch | App starts without crashing; profile screen is displayed. | | Pass/Fail | |
| 2 | Empty name | Clear validation message shown; profile not saved. | | Pass/Fail | Covered by automated test `ProfileFlowTest.savingWithEmptyName_showsValidationError_andDoesNotNavigate` and unit test `ProfileValidationTest`. |
| 3 | Valid profile | Entered values saved; profile summary displayed. | | Pass/Fail | Covered by `ProfileFlowTest.savingWithValidName_showsSummaryScreen`. |
| 4 | Edit profile | Existing values available and can be updated. | | Pass/Fail | Covered by `ProfileFlowTest.editProfile_returnsToFormWithSavedValues`. |
| 5 | Persistence | Saved info still available after closing and reopening the app. | | Pass/Fail | Manual: save a profile, swipe the app away (not just background), relaunch. |
| 6 | Small screen | Content remains readable/usable, no broken layout. | | Pass/Fail | Manual: run on a small emulator profile (e.g. Pixel 4a / small phone). |
| 7 | Second screen size | App remains usable on another device configuration. | | Pass/Fail | Manual: run on a tablet/large-phone emulator profile. |
| 8 | Orientation change | No crash; state/behaviour acceptable, or limitation documented. | | Pass/Fail | Manual: rotate on both the form (mid-typing) and the summary screen. |
| 9 | Rapid Save taps | No crash or unintended duplicate navigation. | | Pass/Fail | The Save button disables itself (`isSaving`) while a save is in flight — see `ProfileViewModel.saveProfile()`. |
| 10 | Keyboard | Fields and primary action remain accessible while entering text. | | Pass/Fail | The form scrolls and applies `imePadding()` so Save stays reachable above the keyboard. |

## Non-functional test

**What was tested:** _fill in, e.g. "startup responsiveness" or "text scaling accessibility"._

**How it was tested:** _fill in, e.g. steps taken, tool used (Logcat timings, system Font size setting, etc.)._

**What was observed:** _fill in the actual result._

## Known issues / limitations

- _fill in anything discovered during testing that wasn't fixed, and why._

## Screenshots / recording

- Main form screen: `docs/screenshots/form.png`
- Saved profile screen: `docs/screenshots/summary.png`
- Screen recording: `docs/recording.mp4` (or a link if too large for the repo)
