package com.display.photos.data.model

import com.google.gson.annotations.SerializedName

data class PhotosResponse(
    @SerializedName("id") val id: String,
    @SerializedName("width") val width: Int,
    @SerializedName("height") val height: Int,
    @SerializedName("urls") val urls: PhotoURL
)

data class PhotoURL(
    @SerializedName("thumb") val imageUrl: String
)
