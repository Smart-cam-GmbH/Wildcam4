package com.appexsoul.cameraimages

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.appexsoul.cameraimages.databinding.ActivityMainBinding
import org.apache.commons.net.ftp.FTPClient
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ImageAdapter
    private lateinit var ftpConfig: FTPConfig
    private val TAG = "FTP_IMAGE_DEBUG"

    data class FTPConfig(
        val host: String,
        val username: String,
        val password: String,
        val folder: String
    )

    private fun loadFTPConfig(): FTPConfig {
        val inputStream = resources.openRawResource(R.raw.ftp_config)
        val json = inputStream.bufferedReader().use { it.readText() }
        val obj = JSONObject(json)
        return FTPConfig(
            obj.getString("host"),
            obj.getString("username"),
            obj.getString("password"),
            obj.getString("folder")
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupUI()
        ftpConfig = loadFTPConfig()
        loadImagesFromFTP()
    }

    private fun setupUI() {
        // Setup RecyclerView
        adapter = ImageAdapter(mutableListOf(), this)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        // Setup FAB
        binding.fabRefresh.setOnClickListener {
            refreshImages()
        }

        // Initial status
        updateStatus("Connecting to FTP server...", true)
    }

    private fun loadImagesFromFTP() {
        val host = ftpConfig.host
        val username = ftpConfig.username
        val password = ftpConfig.password
        val folder = ftpConfig.folder

        Thread {
            val ftpClient = FTPClient()
            try {
                ftpClient.connect(host)
                val success = ftpClient.login(username, password)
                ftpClient.enterLocalPassiveMode()
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE)

                if (success) {
                    runOnUiThread {
                        updateStatus("Loading images...", true)
                    }

                    Log.d(TAG, "Connected to FTP")
                    val files = ftpClient.listFiles(folder)
                    Log.d(TAG, "Total files: ${files.size}")

                    var imageCount = 0
                    for (file in files) {
                        if (file.isFile && file.name.lowercase().endsWith(".jpg")) {
                            Log.d(TAG, "Downloading file: ${file.name}")
                            val localFile = File(cacheDir, file.name)
                            val outputStream = FileOutputStream(localFile)
                            val downloaded = ftpClient.retrieveFile("${folder}${file.name}", outputStream)
                            outputStream.close()

                            if (downloaded) {
                                imageCount++
                                runOnUiThread {
                                    adapter.addImage(localFile.absolutePath)
                                    updateStatus("Loaded $imageCount images", false)
                                }
                            } else {
                                Log.e(TAG, "Failed to download ${file.name}")
                            }
                        }
                    }

                    runOnUiThread {
                        if (imageCount > 0) {
                            updateStatus("$imageCount images loaded successfully", false)
                        } else {
                            updateStatus("No images found", false)
                        }
                    }
                } else {
                    Log.e(TAG, "FTP login failed")
                    runOnUiThread {
                        updateStatus("Connection failed", false)
                        Toast.makeText(this, "Failed to connect to FTP server", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading images: ${e.message}", e)
                runOnUiThread {
                    updateStatus("Error: ${e.message}", false)
                    Toast.makeText(this, "FTP error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } finally {
                try {
                    if (ftpClient.isConnected) {
                        ftpClient.logout()
                        ftpClient.disconnect()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error closing FTP connection: ${e.message}", e)
                }
            }
        }.start()
    }

    private fun refreshImages() {
        adapter.clearImages()
        updateStatus("Refreshing...", true)
        loadImagesFromFTP()
    }

    private fun updateStatus(message: String, showProgress: Boolean) {
        binding.statusSubtitle.text = message
        binding.progressIndicator.visibility = if (showProgress) View.VISIBLE else View.GONE
        
        // Update status icon based on state
        if (showProgress) {
            binding.statusIcon.setImageResource(R.drawable.ic_cloud_download)
        } else {
            binding.statusIcon.setImageResource(R.drawable.ic_refresh)
        }
    }
}
