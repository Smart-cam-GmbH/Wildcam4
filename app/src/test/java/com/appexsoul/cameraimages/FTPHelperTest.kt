package com.appexsoul.cameraimages

import org.junit.Assert.*
import org.junit.Test

class FTPHelperTest {
    private fun callIsImage(fileName: String): Boolean {
        val helper = FTPHelper()
        val method = FTPHelper::class.java.getDeclaredMethod("isImage", String::class.java)
        method.isAccessible = true
        return method.invoke(helper, fileName) as Boolean
    }

    @Test
    fun isImage_acceptsCommonExtensions() {
        val files = listOf("photo.JPG", "image.jpeg", "icon.png", "pic.webp")
        for (file in files) {
            assertTrue("Expected $file to be recognized as image", callIsImage(file))
        }
    }

    @Test
    fun isImage_rejectsUnknownExtensions() {
        val files = listOf("document.pdf", "archive.zip", "script.js")
        for (file in files) {
            assertFalse("Expected $file to not be recognized as image", callIsImage(file))
        }
    }
}
