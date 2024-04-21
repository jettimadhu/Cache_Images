package com.display.photos.data.error.mapper

import android.content.Context
import com.display.photos.R
import com.display.photos.util.Constants

class ErrorMapper(private val context: Context) : ErrorMapperSource {

    override fun getErrorString(errorId: Int): String {
        return context.getString(errorId)
    }

    override val errorsMap: Map<Int, String>
        get() = mapOf(
            Pair(Constants.BAD_REQUEST, getErrorString(R.string.bad_request)),
            Pair(Constants.UNAUTHORIZED, getErrorString(R.string.unauthorized)),
            Pair(Constants.FORBIDDEN, getErrorString(R.string.forbidden)),
            Pair(Constants.NOT_FOUND, getErrorString(R.string.not_found)),
            Pair(Constants.INTERNAL_ERROR, getErrorString(R.string.generic_error)),
        ).withDefault { getErrorString(R.string.generic_error) }
}
