package com.tejasnarendra.studentprofile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.tejasnarendra.studentprofile.data.AreaOfInterest
import com.tejasnarendra.studentprofile.data.EducationLevel
import com.tejasnarendra.studentprofile.data.ProfileRepository
import com.tejasnarendra.studentprofile.data.ProfileValidation
import com.tejasnarendra.studentprofile.data.StudentProfile
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** Which of the two screens is currently shown. */
enum class Screen { FORM, SUMMARY }

/**
 * Everything the UI needs to render either screen. Held as a single
 * immutable snapshot so Compose can recompose only when it actually changes,
 * and so this state survives an Activity recreation (e.g. rotation) because
 * it lives in the ViewModel, not in a composable.
 */
data class ProfileUiState(
    val name: String = "",
    val educationLevel: EducationLevel = EducationLevel.CLASS_10,
    val areaOfInterest: AreaOfInterest = AreaOfInterest.ENGINEERING,
    val nameError: String? = null,
    val screen: Screen = Screen.FORM,
    val isSaving: Boolean = false,
    val savedProfile: StudentProfile? = null
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProfileRepository(application)

    var uiState by mutableStateOf(ProfileUiState())
        private set

    init {
        // On launch, check whether a profile was already saved from a
        // previous session. If so, show the summary screen straight away
        // (this is also what the "persistence" test exercises); otherwise
        // leave the empty form showing.
        viewModelScope.launch {
            val saved = repository.profile.first()
            if (saved != null) {
                uiState = uiState.copy(
                    name = saved.name,
                    educationLevel = saved.educationLevel,
                    areaOfInterest = saved.areaOfInterest,
                    savedProfile = saved,
                    screen = Screen.SUMMARY
                )
            }
        }
    }

    fun onNameChange(value: String) {
        uiState = uiState.copy(name = value, nameError = null)
    }

    fun onEducationLevelChange(value: EducationLevel) {
        uiState = uiState.copy(educationLevel = value)
    }

    fun onAreaOfInterestChange(value: AreaOfInterest) {
        uiState = uiState.copy(areaOfInterest = value)
    }

    /** Validates the form and, if valid, persists it and moves to the summary screen. */
    fun saveProfile() {
        // Guards against rapid double-taps on Save: once a save is in
        // flight, further taps are ignored until it completes.
        if (uiState.isSaving) return

        val error = ProfileValidation.validateName(uiState.name)
        if (error != null) {
            uiState = uiState.copy(nameError = error)
            return
        }

        val profile = StudentProfile(
            name = uiState.name.trim(),
            educationLevel = uiState.educationLevel,
            areaOfInterest = uiState.areaOfInterest
        )

        uiState = uiState.copy(isSaving = true)
        viewModelScope.launch {
            repository.saveProfile(profile)
            uiState = uiState.copy(
                name = profile.name,
                isSaving = false,
                savedProfile = profile,
                screen = Screen.SUMMARY
            )
        }
    }

    /** Returns to the form with the currently saved values pre-filled for editing. */
    fun editProfile() {
        uiState = uiState.copy(screen = Screen.FORM)
    }
}
