package com.tejasnarendra.studentprofile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tejasnarendra.studentprofile.data.StudentProfile

/**
 * The confirmation / profile-summary screen shown right after a save (and
 * on app relaunch, if a profile was already saved).
 */
@Composable
fun ProfileSummaryScreen(
    profile: StudentProfile,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Profile Saved",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryRow(label = "Name", value = profile.name)
                SummaryRow(label = "Current Education Level", value = profile.educationLevel.label)
                SummaryRow(label = "Area of Interest", value = profile.areaOfInterest.label)
            }
        }

        Button(
            onClick = onEdit,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("editButton")
        ) {
            Text("Edit Profile")
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelLarge)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}
