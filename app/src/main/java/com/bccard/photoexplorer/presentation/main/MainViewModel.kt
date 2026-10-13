package com.bccard.photoexplorer.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.bccard.photoexplorer.domain.usecase.MainPhotosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getPhotos: MainPhotosUseCase,
) : ViewModel() {
    val photos = getPhotos().cachedIn(viewModelScope)
}
