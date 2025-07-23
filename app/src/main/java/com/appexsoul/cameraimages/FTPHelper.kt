package com.appexsoul.cameraimages

import android.util.Log
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPFile

class FTPHelper {
    private val TAG = "FTP_IMAGE_DEBUG"
    private val ftpClient = FTPClient()

    fun connectAndListImages(host: String, username: String, password: String, folder: String): List<String> {
        val imageUrls = mutableListOf<String>()

        try {
            ftpClient.connect(host)
            val loginSuccess = ftpClient.login(username, password)
            Log.d(TAG, "FTP login: $loginSuccess")
            if (!loginSuccess) return emptyList()

            ftpClient.enterLocalPassiveMode()
            ftpClient.changeWorkingDirectory(folder)
            Log.d(TAG, "Changed directory to $folder")

            val files: Array<FTPFile> = ftpClient.listFiles()
            Log.d(TAG, "Total files: ${files.size}")

            for (file in files) {
                Log.d(TAG, "Found file: ${file.name}")
                if (!file.isDirectory && isImage(file.name)) {
                    val url = "ftp://$username:$password@$host/$folder/${file.name}"
                    imageUrls.add(url)
                }
            }

            ftpClient.logout()
        } catch (e: Exception) {
            Log.e(TAG, "FTP error: ${e.message}", e)
        } finally {
            if (ftpClient.isConnected) {
                ftpClient.disconnect()
            }
        }

        return imageUrls
    }

    private fun isImage(filename: String): Boolean {
        return filename.endsWith(".jpg", true) ||
                filename.endsWith(".jpeg", true) ||
                filename.endsWith(".png", true) ||
                filename.endsWith(".webp", true)
    }
}

