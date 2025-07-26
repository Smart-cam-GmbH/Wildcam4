package com.appexsoul.cameraimages

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.appexsoul.cameraimages.databinding.ItemImageBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import java.io.File
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

class ImageAdapter(private val imagePaths: MutableList<String>, val context: Context) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val path = imagePaths[position]
        val file = File(path)

        // Load image with Glide
        Glide.with(holder.itemView.context)
            .load(file)
            .placeholder(R.drawable.loader_test)
            .error(R.drawable.loader_test)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.binding.imageView)

        // Set image title and size
        holder.binding.imageTitle.text = file.name
        holder.binding.imageSize.text = formatFileSize(file.length())

        // Download button click listener
        holder.binding.btnDownload.setOnClickListener {
            downloadImage(file)
        }

        // Card click listener for full view (optional)
        holder.itemView.setOnClickListener {
            val intent = android.content.Intent(context, ImageViewerActivity::class.java).apply {
                putExtra(ImageViewerActivity.EXTRA_IMAGE_PATH, file.absolutePath)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = imagePaths.size

    fun addImage(path: String) {
        imagePaths.add(path)
        notifyItemInserted(imagePaths.size - 1)
    }

    fun clearImages() {
        val size = imagePaths.size
        imagePaths.clear()
        notifyItemRangeRemoved(0, size)
    }

    private fun downloadImage(file: File) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(context, "Storage permission required to save file", Toast.LENGTH_SHORT).show()
            return
        }

        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, file.name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
        }

        try {
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            if (uri == null) {
                Toast.makeText(context, "Failed to create destination", Toast.LENGTH_SHORT).show()
                return
            }

            resolver.openOutputStream(uri)?.use { output ->
                file.inputStream().use { input ->
                    input.copyTo(output)
                }
            }

            Toast.makeText(context, "Downloaded to Downloads folder", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("ImageAdapter", "Error downloading file: ${e.message}")
            Toast.makeText(context, "Download failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatFileSize(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(bytes.toDouble()) / log10(1024.0)).toInt()
        
        return DecimalFormat("#,##0.#").format(
            bytes / 1024.0.pow(digitGroups.toDouble())
        ) + " " + units[digitGroups]
    }
}
