package com.appexsoul.cameraimages

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.appexsoul.cameraimages.databinding.ActivityImageViewerBinding
import com.bumptech.glide.Glide

class ImageViewerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val path = intent.getStringExtra(EXTRA_IMAGE_PATH)
        if (path != null) {
            Glide.with(this)
                .load(path)
                .into(binding.fullImageView)
        }

        binding.fullImageView.setOnClickListener { finish() }
    }

    companion object {
        const val EXTRA_IMAGE_PATH = "image_path"
    }
}
