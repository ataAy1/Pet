package com.app.kayipetapp.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.kayipetapp.common.Resource
import com.app.kayipetapp.domain.use_case.LoginUseCase
import com.app.kayipetapp.presentation.auth.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {
    private val _loginState = MutableStateFlow(AuthState())
    val loginState: StateFlow<AuthState> get() = _loginState

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = AuthState(isLoading = true)
            loginUseCase(email, password).collect { resource ->
                _loginState.value = when (resource) {
                    is Resource.Success -> AuthState(isLoading = false, isSuccess = true)
                    is Resource.Error -> AuthState(isLoading = false, error = resource.message ?: "Hatalı Giriş")
                    else -> AuthState(isLoading = false, error = "Unknown error occurred")
                }
            }
        }
    }
}
