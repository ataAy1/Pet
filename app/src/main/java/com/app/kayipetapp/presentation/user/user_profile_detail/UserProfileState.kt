package com.app.kayipetapp.presentation.user.user_profile_detail

import com.app.kayipetapp.domain.models.UserModel

data class UserProfileState(
    val isLoading: Boolean = false,
    val success: String? = null,
    val error: String? = null,
    val data: UserModel? = null
)


