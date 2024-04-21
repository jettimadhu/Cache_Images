package com.display.photos.data.api

import com.display.photos.BuildConfig
import com.display.photos.data.model.PhotosResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiClient {

    @GET("/photos")
    suspend fun getPhotos(
        @Query("page") page: Int = 1,
        @Query("client_id") clientId: String = BuildConfig.API_KEY
    ): Response<List<PhotosResponse>>
}