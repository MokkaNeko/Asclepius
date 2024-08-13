package com.dicoding.asclepius.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageData(
    val currentImageUri: String,
    val label: String,
    val percentageScore: String,
    val inferenceTimeInMillis: Long
): Parcelable
