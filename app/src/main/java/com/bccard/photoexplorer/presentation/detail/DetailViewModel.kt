package com.bccard.photoexplorer.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bccard.photoexplorer.domain.usecase.GetPhotoDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getPhotoDetailUseCase: GetPhotoDetailUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadPhotoDetail(photoId: String) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading
            try {
                val photoDetail = getPhotoDetailUseCase(photoId)
                _uiState.value = DetailUiState.Success(
                    photoDetail = photoDetail,
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = DetailUiState.Error(
                    message = e.message ?: "상세 이미지 정보를 불러오지 못하였습니다.",
                )
            }
        }
    }
}
