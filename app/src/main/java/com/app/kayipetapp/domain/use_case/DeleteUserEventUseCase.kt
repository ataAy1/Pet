package com.app.kayipetapp.domain.use_case

import com.app.kayipetapp.common.Resource
import com.app.kayipetapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteUserEventUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(eventID: String): Flow<Resource<Unit>> {
        return userRepository.deleteUserEvent(eventID)
    }
}