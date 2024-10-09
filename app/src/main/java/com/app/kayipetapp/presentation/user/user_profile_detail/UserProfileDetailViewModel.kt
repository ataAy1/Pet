package com.app.kayipetapp.presentation.user.user_profile_detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.kayipetapp.common.Resource
import com.app.kayipetapp.domain.use_case.DeleteUserAccountUseCase
import com.app.kayipetapp.domain.use_case.DeleteUserEventUseCase
import com.app.kayipetapp.domain.use_case.GetUserProfileUseCase
import com.app.kayipetapp.domain.use_case.SignOutUseCase
import com.app.kayipetapp.domain.use_case.UpdateUserNickUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileDetailViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserNickUseCase: UpdateUserNickUseCase,
    private val deleteUserAccountUseCase: DeleteUserAccountUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _userProfileState = MutableStateFlow(UserProfileState())
    val userProfileState: StateFlow<UserProfileState> = _userProfileState


    private fun updateState(state: UserProfileState) {
        _userProfileState.value = state
    }

    fun updateUserNick(newUserNick: String) = viewModelScope.launch {
        updateState(UserProfileState(isLoading = true))
        updateUserNickUseCase(newUserNick).collect { result ->
            updateState(
                when (result) {
                    is Resource.Loading -> UserProfileState(isLoading = true)
                    is Resource.Error -> UserProfileState(error = result.message ?: "Unknown error")
                    is Resource.Success -> UserProfileState(success = result.message ?: "Nickname updated successfully")
                }
            )
        }
    }

    fun getProfile() = viewModelScope.launch {
        updateState(UserProfileState(isLoading = true))
        getUserProfileUseCase().collect { result ->
            updateState(
                when (result) {
                    is Resource.Loading -> UserProfileState(isLoading = true)
                    is Resource.Error -> UserProfileState(error = result.message ?: "Error loading profile")
                    is Resource.Success -> UserProfileState(data = result.data?.firstOrNull())
                }
            )
        }
    }

    fun deleteUserAccount() = viewModelScope.launch {
        updateState(UserProfileState(isLoading = true))
        deleteUserAccountUseCase().collect { result ->
            updateState(
                when (result) {
                    is Resource.Loading -> UserProfileState(isLoading = true)
                    is Resource.Error -> UserProfileState(error = result.message ?: "Error deleting account")
                    is Resource.Success -> UserProfileState(success = "User account deleted successfully")
                }
            )
        }
    }

    fun signOut() = viewModelScope.launch {
        updateState(UserProfileState(isLoading = true))
        signOutUseCase().collect { result ->
            updateState(
                when (result) {
                    is Resource.Loading -> UserProfileState(isLoading = true)
                    is Resource.Error -> UserProfileState(error = result.message ?: "Error signing out")
                    is Resource.Success -> UserProfileState(success = "Signed out successfully")
                }
            )
        }
    }
}
