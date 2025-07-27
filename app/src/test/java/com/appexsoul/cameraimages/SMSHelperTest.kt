package com.appexsoul.cameraimages

import org.junit.Assert.assertEquals
import org.junit.Test

class SMSHelperTest {
    @Test
    fun parseCommand_trimsAndUppercases() {
        val input = "  capture  "
        val parsed = SMSHelper.parseCommand(input)
        assertEquals("CAPTURE", parsed)
    }
}
