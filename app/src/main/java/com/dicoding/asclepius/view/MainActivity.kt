package com.dicoding.asclepius.view

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.ImageData
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications

class MainActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMainBinding
    private lateinit var imageClassifierHelper: ImageClassifierHelper

    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.galleryButton.setOnClickListener{startGallery()}
        binding.analyzeButton.setOnClickListener{analyzeImage(currentImageUri)}
    }

    private fun startGallery() {
        // TODO: Mendapatkan gambar dari Gallery.
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            currentImageUri= result.data?.data as Uri
            showImage()
        }
    }

    private fun showImage() {
        // TODO: Menampilkan gambar sesuai Gallery yang dipilih.
        currentImageUri?.let { binding.previewImageView.setImageURI(it) }
    }

    private fun analyzeImage(imageUri: Uri?) {
        // TODO: Menganalisa gambar yang berhasil ditampilkan.
        if (imageUri != null) {
            val imageClassifierHelper = ImageClassifierHelper(
                context = this,
                classifierListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(error: String) {
                        showToast(error)
                    }

                    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                        results?.let { classifications ->
                            classifications.forEach { classification ->
                                val categoriesList = classification.categories
                                val label = categoriesList.first().label
                                val score = categoriesList.first().score
                                val percentageScore = (score * 100).toInt()
                                val percentageScoreStr = "$percentageScore%"
                                moveToResult(currentImageUri, label, percentageScoreStr, inferenceTime)
                            }
                        }
                    }
                })

            imageClassifierHelper.classifyStaticImage(imageUri)
        } else {
            showToast("Please choose proper image")
        }
    }

    private fun moveToResult(
        currentImageUri: Uri?,
        label: String,
        percentageScore: String,
        inferenceTimeInMillis: Long
    ) {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra(
                ResultActivity.EXTRA_IMAGE_URI, ImageData(
                    currentImageUri = currentImageUri.toString(),
                    label = label,
                    percentageScore = percentageScore,
                    inferenceTimeInMillis = inferenceTimeInMillis
                )
            )

        }
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}