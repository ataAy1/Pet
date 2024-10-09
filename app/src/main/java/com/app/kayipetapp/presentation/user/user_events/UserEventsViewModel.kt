package com.app.kayipetapp.presentation.user.user_events

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.kayipetapp.common.Resource
import com.app.kayipetapp.domain.use_case.DeleteUserEventUseCase
import com.app.kayipetapp.domain.use_case.GetUserEventsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserEventsViewModel @Inject constructor(
    private val getUserEventsUseCase: GetUserEventsUseCase,
    private val deleteUserEventUseCase: DeleteUserEventUseCase
) : ViewModel() {
    private val _UserEventState = MutableStateFlow<UserEventState>(UserEventState())
    val userEventState: StateFlow<UserEventState> = _UserEventState

    fun getUserEvents() {
        viewModelScope.launch {
            getUserEventsUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _UserEventState.value = UserEventState(isLoading = true)
                    }
                    is Resource.Error -> {
                        _UserEventState.value =
                            UserEventState(error = result.message ?: "Unknown error")
                    }
                    is Resource.Success -> {
                        _UserEventState.value = UserEventState(data = result.data)
                    }
                }
            }
        }
    }

    fun deleteUserEvents(eventID: String) {
        viewModelScope.launch {
            deleteUserEventUseCase(eventID).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _UserEventState.value = UserEventState(isLoading = true)
                    }
                    is Resource.Error -> {
                        _UserEventState.value = UserEventState(error = result.message ?: "Unknown error")
                    }
                    is Resource.Success -> {
                        _UserEventState.value = UserEventState(success = "Event deleted successfully")
                    }
                }
            }
        }
    }
}
