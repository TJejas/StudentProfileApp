package com.tejasnarendra.studentprofile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tejasnarendra.studentprofile.data.AreaOfInterest
import com.tejasnarendra.studentprofile.data.EducationLevel

/**
 * The data-entry screen: name, education level, area of interest, and Save.
 *
 * All state (text typed, dropdown selections, validation error) is hoisted
 * up to [ProfileViewModel] via the callback parameters below, rather than
 * kept inside this composable, so nothing is lost if the screen recomposes
 * or the Activity is recreated.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileFormScreen(
    name: String,
    nameError: String?,
    educationLevel: EducationLevel,
    areaOfInterest: AreaOfInterest,
    isSaving: Boolean,
    onNameChange: (String) -> Unit,
    onEducationLevelChange: (EducationLevel) -> Unit,
    onAreaOfInterestChange: (AreaOfInterest) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            // Scrolling + imePadding keep the Save button reachable when the
            // keyboard is open on small screens, instead of it being pushed
            // off-screen or hidden behind the keyboard.
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Student Profile",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Name") },
            isError = nameError != null,
            supportingText = {
                if (nameError != null) Text(nameError)
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("nameField")
        )

        LabeledDropdown(
            label = "Current Education Level",
            selectedLabel = educationLevel.label,
            options = EducationLevel.entries,
            optionLabel = { it.label },
            onOptionSelected = onEducationLevelChange,
            testTag = "educationLevelField"
        )

        LabeledDropdown(
            label = "Area of Interest",
            selectedLabel = areaOfInterest.label,
            options = AreaOfInterest.entries,
            optionLabel = { it.label },
            onOptionSelected = onAreaOfInterestChange,
            testTag = "areaOfInterestField"
        )

        Button(
            onClick = onSave,
            enabled = !isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("saveButton")
        ) {
            Text(if (isSaving) "Saving..." else "Save Profile")
        }
    }
}

/**
 * A reusable read-only dropdown built on Material3's ExposedDropdownMenuBox.
 * Used for both Education Level and Area of Interest so a fixed, valid set
 * of options is enforced (the user cannot type an out-of-range value).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> LabeledDropdown(
    label: String,
    selectedLabel: String,
    options: List<T>,
    optionLabel: (T) -> String,
    onOptionSelected: (T) -> Unit,
    testTag: String
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.testTag(testTag)
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option)) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
