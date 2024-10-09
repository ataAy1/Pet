package com.app.kayipetapp.presentation.auth.resetPassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.kayipetapp.common.Resource
import com.app.kayipetapp.domain.use_case.ResetPasswordUseCase
import com.app.kayipetapp.presentation.auth.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {
    private val _resetState = MutableStateFlow(AuthState())
    val resetState: StateFlow<AuthState> get() = _resetState

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _resetState.value = AuthState(isLoading = true)
            resetPasswordUseCase(email).collect { resource ->
                _resetState.value = when (resource) {
                    is Resource.Success -> AuthState(isLoading = false, isSuccess = true)
                    is Resource.Error -> AuthState(isLoading = false, error = resource.message ?: "Unknown error")
                    else -> AuthState(isLoading = false, error = "Unknown error occurred")
                }
            }
        }
    }
}
