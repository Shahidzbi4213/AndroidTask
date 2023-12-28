package com.gulehri.androidtask.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Image(
    val path: String,
    val view_type: Int = 0
) : Parcelable
