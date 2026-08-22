package com.tejasnarendra.studentprofile.data

/**
 * The education levels a student profile can be saved with.
 * [label] is the human-readable text shown in the UI; [name] (the enum
 * constant name) is what gets persisted, so the label can be reworded later
 * without touching saved data.
 */
enum class EducationLevel(val label: String) {
    CLASS_10("Class 10"),
    CLASS_11("Class 11"),
    CLASS_12("Class 12"),
    BACHELORS("Bachelor's")
}

/** The areas of interest a student profile can be saved with. */
enum class AreaOfInterest(val label: String) {
    ENGINEERING("Engineering"),
    MEDICAL("Medical"),
    CA("CA"),
    OTHER("Other")
}

/** A single student's profile as captured by the form. */
data class StudentProfile(
    val name: String,
    val educationLevel: EducationLevel,
    val areaOfInterest: AreaOfInterest
)
