package com.app.kayipetapp.domain.use_case

import com.app.kayipetapp.common.Resource
import com.app.kayipetapp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke(email: String): Flow<Resource<Unit>> {
        return authRepository.resetPassword(email)
    }

}