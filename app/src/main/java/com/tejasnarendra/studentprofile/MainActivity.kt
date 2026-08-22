package com.tejasnarendra.studentprofile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.tejasnarendra.studentprofile.data.StudentProfile
import com.tejasnarendra.studentprofile.ui.ProfileFormScreen
import com.tejasnarendra.studentprofile.ui.ProfileSummaryScreen
import com.tejasnarendra.studentprofile.ui.theme.StudentProfileAppTheme

class MainActivity : ComponentActivity() {

    // Scoped to the Activity so it (and the state inside it) survives
    // configuration changes such as a screen rotation.
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudentProfileAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val uiState = viewModel.uiState

                    when (uiState.screen) {
                        Screen.FORM -> ProfileFormScreen(
                            name = uiState.name,
                            nameError = uiState.nameError,
                            educationLevel = uiState.educationLevel,
                            areaOfInterest = uiState.areaOfInterest,
                            isSaving = uiState.isSaving,
                            onNameChange = viewModel::onNameChange,
                            onEducationLevelChange = viewModel::onEducationLevelChange,
                            onAreaOfInterestChange = viewModel::onAreaOfInterestChange,
                            onSave = viewModel::saveProfile
                        )

                        Screen.SUMMARY -> ProfileSummaryScreen(
                            profile = uiState.savedProfile ?: StudentProfile(
                                name = uiState.name,
                                educationLevel = uiState.educationLevel,
                                areaOfInterest = uiState.areaOfInterest
                            ),
                            onEdit = viewModel::editProfile
                        )
                    }
                }
            }
        }
    }
}
