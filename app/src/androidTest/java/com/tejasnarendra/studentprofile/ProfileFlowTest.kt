package com.tejasnarendra.studentprofile

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

/**
 * End-to-end UI tests covering the "Empty name", "Valid profile" and
 * "Edit profile" cases from the test plan, run on-device/emulator via
 * Compose's testing APIs.
 */
class ProfileFlowTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun savingWithEmptyName_showsValidationError_andDoesNotNavigate() {
        composeRule.onNodeWithTag("saveButton").performClick()

        composeRule.onNodeWithText("Name cannot be empty").assertIsDisplayed()
        // Still on the form: the name field should still be present.
        composeRule.onNodeWithTag("nameField").assertIsDisplayed()
    }

    @Test
    fun savingWithValidName_showsSummaryScreen() {
        composeRule.onNodeWithTag("nameField").performTextInput("Asha Verma")
        composeRule.onNodeWithTag("saveButton").performClick()

        composeRule.onNodeWithText("Profile Saved").assertIsDisplayed()
        composeRule.onNodeWithText("Asha Verma").assertIsDisplayed()
    }

    @Test
    fun editProfile_returnsToFormWithSavedValues() {
        composeRule.onNodeWithTag("nameField").performTextInput("Asha Verma")
        composeRule.onNodeWithTag("saveButton").performClick()

        composeRule.onNodeWithTag("editButton").performClick()

        // Back on the form, the previously saved name is pre-filled.
        composeRule.onNodeWithText("Asha Verma").assertIsDisplayed()
        composeRule.onNodeWithTag("saveButton").assertIsDisplayed()
    }
}
