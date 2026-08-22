package com.tejasnarendra.studentprofile

import com.tejasnarendra.studentprofile.data.ProfileValidation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProfileValidationTest {

    @Test
    fun `empty name returns an error`() {
        assertEquals("Name cannot be empty", ProfileValidation.validateName(""))
    }

    @Test
    fun `whitespace-only name returns an error`() {
        assertEquals("Name cannot be empty", ProfileValidation.validateName("   "))
    }

    @Test
    fun `non-empty name is valid`() {
        assertNull(ProfileValidation.validateName("Asha Verma"))
    }

    @Test
    fun `name with surrounding whitespace is valid`() {
        assertNull(ProfileValidation.validateName("  Asha Verma  "))
    }
}
