package com.appexsoul.cameraimages

import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPFile
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for [FTPHelper.connectAndListImages] using a mocked [FTPClient].
 */
class FTPHelperConnectTest {

    /** Simple fake FTP client used for tests. */
    private class FakeFTPClient(
        private val loginSuccess: Boolean,
        private val files: Array<FTPFile> = emptyArray()
    ) : FTPClient() {
        private var connected = false

        override fun connect(host: String?) {
            connected = true
        }

        override fun login(username: String?, password: String?): Boolean {
            return loginSuccess
        }

        override fun enterLocalPassiveMode() {
            // no-op
        }

        override fun changeWorkingDirectory(pathname: String?): Boolean {
            return true
        }

        override fun listFiles(): Array<FTPFile> {
            return files
        }

        override fun logout(): Boolean {
            return true
        }

        override fun isConnected(): Boolean {
            return connected
        }

        override fun disconnect() {
            connected = false
        }
    }

    private fun injectClient(helper: FTPHelper, client: FTPClient) {
        val field = FTPHelper::class.java.getDeclaredField("ftpClient")
        field.isAccessible = true
        field.set(helper, client)
    }

    @Test
    fun connectAndListImages_success() {
        val file1 = FTPFile().apply {
            name = "img1.jpg"
            setType(FTPFile.FILE_TYPE)
        }
        val file2 = FTPFile().apply {
            name = "img2.png"
            setType(FTPFile.FILE_TYPE)
        }
        val dir = FTPFile().apply {
            name = "dir"
            setType(FTPFile.DIRECTORY_TYPE)
        }
        val fakeClient = FakeFTPClient(true, arrayOf(file1, file2, dir))
        val helper = FTPHelper()
        injectClient(helper, fakeClient)

        val urls = helper.connectAndListImages("host", "user", "pass", "folder")

        val expected = listOf(
            "ftp://user:pass@host/folder/img1.jpg",
            "ftp://user:pass@host/folder/img2.png"
        )
        assertEquals(expected, urls)
    }

    @Test
    fun connectAndListImages_failedLogin() {
        val fakeClient = FakeFTPClient(false)
        val helper = FTPHelper()
        injectClient(helper, fakeClient)

        val urls = helper.connectAndListImages("host", "user", "pass", "folder")

        assertTrue(urls.isEmpty())
    }
}
