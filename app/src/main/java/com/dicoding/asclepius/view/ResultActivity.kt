package com.dicoding.asclepius.view

import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.ImageData
import com.dicoding.asclepius.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.
        getData()
    }

    private fun getData() {
        val imageData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_IMAGE_URI, ImageData::class.java)
        } else {
            intent.getParcelableExtra(EXTRA_IMAGE_URI)
        }

        val imageUri = Uri.parse(imageData?.currentImageUri.orEmpty())
        val label = imageData?.label.orEmpty()
        val score = imageData?.percentageScore.orEmpty()
        val inferenceTime = imageData?.inferenceTimeInMillis.toString()

        binding.apply {
            imageUri?.let { showImage(imageUri) }
            resultText.text = "Hasil Analisis \n$label $score \nWaktu yang dibutuhkan $inferenceTime ms"
        }
    }

    private fun showImage(imageUri: Uri) {
        binding.resultImage.setImageURI(imageUri)
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
    }
}