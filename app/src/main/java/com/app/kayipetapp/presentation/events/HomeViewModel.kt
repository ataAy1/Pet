package com.app.kayipetapp.presentation.events

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.kayipetapp.common.Resource
import com.app.kayipetapp.domain.use_case.GetEventsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getEventsUseCase: GetEventsUseCase
) : ViewModel() {

    private val _homeState = MutableStateFlow(HomeState())
    val homeState: StateFlow<HomeState> = _homeState

    fun getEvents() {
        viewModelScope.launch {
            getEventsUseCase().onEach { result ->
                _homeState.value = when (result) {
                    is Resource.Loading -> {
                        _homeState.value.copy(isLoading = true)
                    }
                    is Resource.Error -> {
                        _homeState.value.copy(isLoading = false, error = result.message ?: "Unknown error")
                    }
                    is Resource.Success -> {
                        _homeState.value.copy(isLoading = false, data = result.data, error = "")
                    }
                }
            }.launchIn(this)
        }
    }
}
