package com.display.photos.data.model

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Failure(val statusCode: Int?, val message: String?): ApiResult<Nothing>()
}