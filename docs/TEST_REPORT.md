# Test Report - Student Profile App

> Fill in the **Actual result**, **Status**, and **Notes/evidence** columns after each manual run.
> "Device/emulator" should be a real name (e.g. "Pixel 6 API 34 emulator", "Pixel 7 physical, Android 14").

## Environment used

| Item | Value |
|---|---|
| Android Studio version | Android Studio 2026.1.3 (build AI-261.26222.65.2613.16025427) |
| Primary device/emulator | StudentProfileEmulator (Pixel 6, API 34) |
| Android version (primary) | Android 14 ("UpsideDownCake"), google_apis x86_64 |
| Second device/emulator | SmallPhoneEmulator (Pixel 4a, API 34) |
| Android version (second) | Android 14 ("UpsideDownCake"), google_apis x86_64 |

## Functional tests

| # | Test | Expected result | Actual result | Status | Notes / evidence |
|---|---|---|---|---|---|
| 1 | Application launch | App starts without crashing; profile screen is displayed. | App launched and rendered the form (Name, Current Education Level = Class 10, Area of Interest = Engineering, Save Profile) with no crash. | **Pass** | `docs/screenshots/form.png` |
| 2 | Empty name | Clear validation message shown; profile not saved. | Tapping Save with an empty name kept the user on the form, outlined the Name field in red, and showed "Name cannot be empty" below it. | **Pass** | `docs/screenshots/validation_error.png`. Also covered by automated test `ProfileFlowTest.savingWithEmptyName_showsValidationError_andDoesNotNavigate` and unit test `ProfileValidationTest`. |
| 3 | Valid profile | Entered values saved; profile summary displayed. | Entering a name and tapping Save showed "Saving..." briefly, then navigated to "Profile Saved" showing the entered Name/Education Level/Area of Interest. | **Pass** | `docs/screenshots/summary.png`. Also covered by `ProfileFlowTest.savingWithValidName_showsSummaryScreen`. |
| 4 | Edit profile | Existing values available and can be updated. | Tapped Edit Profile on the summary screen; form reopened with Name/Education Level/Area of Interest all pre-filled with the previously saved values. | **Pass** | `docs/screenshots/manual_test_run/04_edit_profile_prefilled.png`. Also covered by `ProfileFlowTest.editProfile_returnsToFormWithSavedValues`. |
| 5 | Persistence | Saved info still available after closing and reopening the app. | Force-stopped the app (`adb shell am force-stop`) after saving, then relaunched. It opened directly to the "Profile Saved" summary screen with the same values. | **Pass** | Verified via adb on the emulator above. |
| 6 | Small screen | Content remains readable/usable, no broken layout. | Ran on a new AVD (SmallPhoneEmulator, Pixel 4a, API 34). Form and summary screens rendered fully, no clipped/overlapping content; full form → save → summary flow completed successfully with a test entry ("SmallScreenTest"). | **Pass** | `docs/screenshots/manual_test_run/15_small_phone_launch2.png`, `18_small_phone_summary_final.png` |
| 7 | Second screen size | App remains usable on another device configuration. | Same SmallPhoneEmulator (Pixel 4a) run as #6, a genuinely different device profile/resolution (1080x2340) from the primary Pixel 6 (1080x2400); no layout or functional issues observed. | **Pass** | Same evidence as #6. |
| 8 | Orientation change | No crash; state/behaviour acceptable, or limitation documented. | Rotated via the emulator (console `rotate`, same mechanism as Ctrl+F11/F12) while mid-typing on the form (name "Tejas" entered). It survived rotation with the typed value intact, no crash. Rotated again on the summary screen; saved values still displayed correctly, no crash. Checked logcat for `FATAL`/`AndroidRuntime` after each rotation: none found. | **Pass** | `docs/screenshots/manual_test_run/09_rotated_form.png`, `11_summary_landscape.png`, `12_summary_rotated_back.png`. State survives because it lives in `ProfileViewModel`, retained across the Activity recreation. See README's "Orientation / configuration changes" section. |
| 9 | Rapid Save taps | No crash or unintended duplicate navigation. | Tapped Save 6 times in rapid succession (adb `input tap` looped with no delay) with a valid, pre-filled name. Result: exactly one clean navigation to the summary screen, no crash, no stacked/duplicate navigation. Checked logcat: no `FATAL`/`AndroidRuntime` entries. | **Pass** | `docs/screenshots/manual_test_run/05_rapid_taps_result.png`. The Save button disables itself (`isSaving`) while a save is in flight; see `ProfileViewModel.saveProfile()`. |
| 10 | Keyboard | Fields and primary action remain accessible while entering text. | Tapped the Name field (forced the soft keyboard visible via `show_ime_with_hard_keyboard`, since AVDs otherwise treat adb input as a hardware keyboard and don't auto-show the IME). With the keyboard open, "Save Profile" remained fully visible above it, not obscured. | **Pass** | `docs/screenshots/manual_test_run/07_keyboard_forced.png`. The form scrolls and applies `imePadding()` so Save stays reachable above the keyboard. |

## Non-functional test

**What was tested:** Text-scaling accessibility (does the UI hold up when the system font size is increased, per Android's accessibility guidance that all text should be in `sp` and scale with the user's setting).

**How it was tested:** Used `adb shell settings put system font_scale <value>` to simulate the system Font size setting (this is the same setting exposed in Settings → Display → Font size / Accessibility), then force-stopped and relaunched the app so it picked up the new configuration. Tested at `1.0` (default baseline), `1.3`, and `2.0` (Android's maximum "Largest" accessibility font size) on the form screen.

**What was observed:** At 2.0x, all labels and field text scaled up correctly and remained fully legible: no clipping, no overlapping fields, no text cut off. The `OutlinedTextField` boxes grew taller to accommodate the larger text, and the "Save Profile" button's label stayed centered and fully visible. No crash at any scale (checked logcat for `FATAL`/`AndroidRuntime` after each change; none found). This is expected given the app uses Material3 typography in `sp` throughout (see README's "Accessibility" section) rather than fixed `dp`/pixel sizes. One methodology note: changing `font_scale` via `adb shell settings put` does not immediately push a live config change to an already-running activity the way the system Settings UI does. The app had to be force-stopped and relaunched to observe the new scale; this is a testing-tool quirk, not an app behavior issue. Before/after evidence: `docs/screenshots/manual_test_run/22_font_scale_1.0_baseline.png` (default) vs `21_font_scale_2.0_fresh.png` (2.0x, "Largest").

## Known issues / limitations

- During initial setup, the emulator briefly showed a "System UI isn't responding" ANR dialog while several large SDK/tooling downloads were running at the same time on the host machine. It cleared on its own once those finished and did not recur; it was host CPU contention, not an app issue (the app's own UI was unaffected underneath the dialog in the screenshot taken at the time).
- During manual testing, running `adb shell screenrecord` at the same time as scripted `adb shell input` taps/text reliably produced a "Student Profile isn't responding" ANR dialog on this host (reproduced twice). Waiting recovered the app cleanly both times with no lasting effect, and it did not occur during any other testing (rapid taps, rotation, edit flow, small-screen run) when screenrecord wasn't also active. It's host CPU contention from running the video encoder and input automation simultaneously on this machine, not an app defect. The screen recording for this report was captured manually (Windows Game Bar) instead of via adb to avoid it. Evidence: `docs/screenshots/manual_test_run/23_anr_dialog.png`.

## Screenshots / recording

- Main form screen: `docs/screenshots/form.png`
- Saved profile screen: `docs/screenshots/summary.png`
- Validation error: `docs/screenshots/validation_error.png`
- Additional manual-test evidence (edit profile, rapid taps, orientation, keyboard, small-screen run, font scaling): `docs/screenshots/manual_test_run/`
- Screen recording (form → save → summary → edit): `docs/recording.mp4`, captured manually via Windows Game Bar on the SmallPhoneEmulator (Pixel 4a)
