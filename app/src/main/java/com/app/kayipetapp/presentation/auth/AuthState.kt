package com.app.kayipetapp.presentation.auth

import com.app.kayipetapp.domain.models.Event
import com.app.kayipetapp.domain.models.UserModel


data class AuthState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String = ""
)
