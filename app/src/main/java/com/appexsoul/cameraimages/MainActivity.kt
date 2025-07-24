package com.appexsoul.cameraimages

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.appexsoul.cameraimages.databinding.ActivityMainBinding
import org.apache.commons.net.ftp.FTPClient
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity(), ImageAdapter.OnImageDownloadListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ImageAdapter
    private val TAG = "FTP_IMAGE_DEBUG"
    private val REQUEST_WRITE_PERMISSION = 1001
    private var pendingDownload: File? = null

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
        loadImagesFromFTP()
    }

    private fun setupUI() {
        // Setup RecyclerView
        adapter = ImageAdapter(mutableListOf(), this, this)
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
        val host = "213.3.5.20"
        val username = "Wildcam"
        val password = "Quickcam_02"
        val folder = "/"

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

    override fun onImageDownloadRequested(file: File) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveImage(file)
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                saveImage(file)
            } else {
                pendingDownload = file
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_WRITE_PERMISSION
                )
            }
        }
    }

    private fun saveImage(file: File) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, file.name)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                uri?.let {
                    contentResolver.openOutputStream(it)?.use { output ->
                        file.inputStream().copyTo(output)
                    }
                }
            } else {
                val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, file.name)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(MediaStore.MediaColumns.DATA, File(downloads, file.name).absolutePath)
                }
                val uri = contentResolver.insert(MediaStore.Files.getContentUri("external"), values)
                uri?.let {
                    contentResolver.openOutputStream(it)?.use { output ->
                        file.inputStream().copyTo(output)
                    }
                }
                MediaScannerConnection.scanFile(
                    this,
                    arrayOf(File(downloads, file.name).absolutePath),
                    null,
                    null
                )
            }
            Toast.makeText(this, "Downloaded to Downloads folder", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "Error downloading file: ${e.message}")
            Toast.makeText(this, "Download failed: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            pendingDownload = null
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_WRITE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pendingDownload?.let { saveImage(it) }
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
