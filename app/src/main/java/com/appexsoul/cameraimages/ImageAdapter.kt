package com.appexsoul.cameraimages

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.appexsoul.cameraimages.databinding.ItemImageBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import java.io.File
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

class ImageAdapter(
    private val imagePaths: MutableList<String>,
    val context: Context,
    private val downloadListener: OnImageDownloadListener
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    interface OnImageDownloadListener {
        fun onImageDownloadRequested(file: File)
    }

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
            downloadListener.onImageDownloadRequested(file)
        }

        // Card click listener for full view (optional)
        holder.itemView.setOnClickListener {
            // You can implement image preview here
            Toast.makeText(context, "Viewing ${file.name}", Toast.LENGTH_SHORT).show()
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


    private fun formatFileSize(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(bytes.toDouble()) / log10(1024.0)).toInt()
        
        return DecimalFormat("#,##0.#").format(
            bytes / 1024.0.pow(digitGroups.toDouble())
        ) + " " + units[digitGroups]
    }
}
