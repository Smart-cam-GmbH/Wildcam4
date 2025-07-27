package com.appexsoul.cameraimages

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/** Instrumentation test to verify FTP config JSON can be loaded. */
@RunWith(AndroidJUnit4::class)
class FTPConfigInstrumentedTest {
    @Test
    fun ftpConfig_isParsedCorrectly() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val inputStream = context.resources.openRawResource(R.raw.ftp_config)
        val json = inputStream.bufferedReader().use { it.readText() }
        val obj = JSONObject(json)

        assertEquals("213.3.5.20", obj.getString("host"))
        assertEquals("Wildcam", obj.getString("username"))
        assertEquals("Quickcam_02", obj.getString("password"))
        assertEquals("/", obj.getString("folder"))
    }
}
