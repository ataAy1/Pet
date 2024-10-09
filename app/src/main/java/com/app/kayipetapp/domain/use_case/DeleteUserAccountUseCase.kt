package com.app.kayipetapp.domain.use_case

import com.app.kayipetapp.common.Resource
import com.app.kayipetapp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteUserAccountUseCase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke(): Flow<Resource<Unit>> {
        return authRepository.deleteAccount()
    }

}