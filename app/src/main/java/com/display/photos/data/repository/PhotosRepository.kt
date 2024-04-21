package com.display.photos.data.repository

import com.display.photos.data.api.ApiClient
import com.display.photos.data.error.mapper.ErrorMapper
import com.display.photos.data.model.PhotosResponse
import com.display.photos.data.model.ApiResult
import com.display.photos.util.Constants

class PhotosRepository(private val apiClient: ApiClient, private val errorMapper: ErrorMapper) {

    suspend fun getPhotos(page: Int = 1): ApiResult<List<PhotosResponse>> {
        runCatching {
            apiClient.getPhotos(page)
        }.onSuccess { response ->
            return if (response.isSuccessful) {
                val data = response.body()
                ApiResult.Success(data ?: listOf())
            } else {
                val statusCode = response.code()
                var message: String? = response.message()
                if (message.isNullOrBlank()) {
                    message = errorMapper.errorsMap[statusCode]
                }
                ApiResult.Failure(statusCode, message)
            }
        }.onFailure { throwable ->
            throwable.printStackTrace()
            return ApiResult.Failure(Constants.INTERNAL_ERROR, throwable.message)
        }

        return ApiResult.Failure(
            Constants.INTERNAL_ERROR,
            errorMapper.errorsMap[Constants.INTERNAL_ERROR]
        )
    }
}