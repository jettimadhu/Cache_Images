package com.display.photos.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.display.photos.data.model.PhotosResponse
import com.display.photos.data.model.ApiResult
import com.display.photos.data.repository.PhotosRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repo: PhotosRepository) : ViewModel() {

    private var currentPage = 1
    private var canRequestMore = false

    private val _photosResponseLiveData = MutableLiveData<List<PhotosResponse>>()
    val photosResponseLiveData: LiveData<List<PhotosResponse>>
        get() = _photosResponseLiveData

    private val _errorLiveData = MutableLiveData<String?>()
    val errorLiveData: LiveData<String?>
        get() = _errorLiveData

    private val totalPhotosList = mutableListOf<PhotosResponse>()

    init {
        getPhotos()
    }

    /**
     * It fetches the image details from the network through API.
     */
    fun getPhotos() {
        canRequestMore = false
        viewModelScope.launch {
            when (val response = repo.getPhotos(currentPage)) {
                is ApiResult.Success -> {
                    _photosResponseLiveData.value = response.data
                    totalPhotosList.addAll(response.data)
                    currentPage++
                    canRequestMore = true
                }

                is ApiResult.Failure -> {
                    _errorLiveData.value = response.message
                }
            }
        }
    }

    fun canRequestMore() = canRequestMore

    fun getTotalPhotosList() = totalPhotosList

    fun clear() {
        _photosResponseLiveData.value = mutableListOf()
    }
}