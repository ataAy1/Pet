package com.app.kayipetapp.domain.use_case

import com.app.kayipetapp.common.Resource
import com.app.kayipetapp.domain.models.Event
import com.app.kayipetapp.domain.models.UserModel
import com.app.kayipetapp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterUseCase @Inject constructor(private val authRepository: AuthRepository) {
    operator fun invoke(userModel: UserModel): Flow<Resource<UserModel>> {
        return authRepository.createUser(userModel = userModel)
    }
}