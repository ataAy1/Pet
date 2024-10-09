package com.app.kayipetapp.presentation.events_add

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.kayipetapp.common.Resource
import com.app.kayipetapp.domain.models.Event
import com.app.kayipetapp.domain.use_case.AddEventsUseCase
import com.app.kayipetapp.domain.use_case.GetEventsUseCase
import com.app.kayipetapp.presentation.events.HomeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EventAddViewModel @Inject constructor(
    private val addEventsUseCase: AddEventsUseCase
) : ViewModel() {

    private val _eventAddState = MutableStateFlow<EventAddState>(EventAddState())
    val eventAddState: StateFlow<EventAddState> = _eventAddState

    fun addEvent(event: Event) {
        viewModelScope.launch {

            addEventsUseCase(event).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _eventAddState.value = EventAddState(isLoading = true)
                    }
                    is Resource.Error -> {
                        _eventAddState.value = EventAddState(
                            isLoading = false,
                            error = result.message ?: "An unknown error occurred."
                        )
                    }
                    is Resource.Success -> {
                        _eventAddState.value = EventAddState(
                            isLoading = false,
                            isSuccess = true
                        )
                    }
                }
            }
        }
    }
}