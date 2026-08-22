package com.tejasnarendra.studentprofile.data

/**
 * Pure validation logic, kept separate from the ViewModel so it can be unit
 * tested on the JVM without needing an Android framework/emulator (see
 * ProfileValidationTest).
 */
object ProfileValidation {

    /** Returns an error message if [rawName] is not a valid profile name, or null if it is valid. */
    fun validateName(rawName: String): String? {
        return if (rawName.trim().isEmpty()) "Name cannot be empty" else null
    }
}
