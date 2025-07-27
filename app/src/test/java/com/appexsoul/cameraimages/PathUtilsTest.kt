package com.appexsoul.cameraimages

import org.junit.Assert.assertEquals
import org.junit.Test

class PathUtilsTest {
    @Test
    fun buildRemotePath_addsSlashIfMissing() {
        val result = buildRemotePath("/images", "pic.jpg")
        assertEquals("/images/pic.jpg", result)
    }

    @Test
    fun buildRemotePath_preservesExistingSlash() {
        val result = buildRemotePath("/images/", "pic.jpg")
        assertEquals("/images/pic.jpg", result)
    }
}
