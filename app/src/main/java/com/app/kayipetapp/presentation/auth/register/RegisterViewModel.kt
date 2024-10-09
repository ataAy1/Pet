package com.app.kayipetapp.presentation.auth.register

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.kayipetapp.common.Resource
import com.app.kayipetapp.domain.models.UserModel
import com.app.kayipetapp.domain.use_case.RegisterUseCase
import com.app.kayipetapp.presentation.auth.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {
    private val _registrationState = MutableStateFlow(AuthState())
    val registrationState: StateFlow<AuthState> get() = _registrationState

    fun registerUser(userModel: UserModel) {
        _registrationState.value = AuthState(isLoading = true)
        viewModelScope.launch {
            registerUseCase(userModel).collect { resource ->
                _registrationState.value = when (resource) {
                    is Resource.Success -> AuthState(isLoading = false, isSuccess = true)
                    is Resource.Error -> AuthState(isLoading = false, error = resource.message ?: "Unknown error")
                    else -> AuthState(isLoading = false, error = "Unknown error occurred")
                }
            }
        }
    }
}
