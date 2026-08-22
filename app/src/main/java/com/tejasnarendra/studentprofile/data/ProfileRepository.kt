package com.tejasnarendra.studentprofile.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "student_profile"
)

/**
 * Reads and writes the single saved [StudentProfile] using Jetpack DataStore
 * (Preferences DataStore).
 *
 * Why DataStore and not something else:
 * - There is exactly one small, flat record to persist (three fields), not a
 *   collection of rows or relational data, so a database (Room/SQLite) would
 *   add schema/DAO/migration overhead this task doesn't need.
 * - Plain SharedPreferences works too, but its API is synchronous/callback
 *   based and easy to misuse on the main thread. DataStore is built on
 *   Kotlin coroutines/Flow, is asynchronous by design, and is the mechanism
 *   Google now recommends over SharedPreferences for new code.
 * - It survives process death and app restarts, which is exactly what
 *   "profile must remain available after the app is closed and reopened"
 *   requires.
 */
class ProfileRepository(context: Context) {

    private val dataStore = context.applicationContext.dataStore

    private object Keys {
        val NAME = stringPreferencesKey("name")
        val EDUCATION_LEVEL = stringPreferencesKey("education_level")
        val AREA_OF_INTEREST = stringPreferencesKey("area_of_interest")
    }

    /** Emits the saved profile, or null if none has been saved yet. */
    val profile: Flow<StudentProfile?> = dataStore.data.map { prefs ->
        val name = prefs[Keys.NAME]
        val educationLevelName = prefs[Keys.EDUCATION_LEVEL]
        val areaOfInterestName = prefs[Keys.AREA_OF_INTEREST]

        if (name.isNullOrBlank() || educationLevelName == null || areaOfInterestName == null) {
            null
        } else {
            StudentProfile(
                name = name,
                educationLevel = runCatching { EducationLevel.valueOf(educationLevelName) }
                    .getOrDefault(EducationLevel.CLASS_10),
                areaOfInterest = runCatching { AreaOfInterest.valueOf(areaOfInterestName) }
                    .getOrDefault(AreaOfInterest.ENGINEERING)
            )
        }
    }

    suspend fun saveProfile(profile: StudentProfile) {
        dataStore.edit { prefs ->
            prefs[Keys.NAME] = profile.name
            prefs[Keys.EDUCATION_LEVEL] = profile.educationLevel.name
            prefs[Keys.AREA_OF_INTEREST] = profile.areaOfInterest.name
        }
    }
}
