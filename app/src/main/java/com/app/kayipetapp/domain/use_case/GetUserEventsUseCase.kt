package com.app.kayipetapp.domain.use_case

import com.app.kayipetapp.common.Resource
import com.app.kayipetapp.domain.models.Event
import com.app.kayipetapp.domain.models.UserModel
import com.app.kayipetapp.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserEventsUseCase @Inject constructor(private  val userRepository: UserRepository) {
    suspend operator fun invoke(): Flow<Resource<MutableList<Event>>> {
        return userRepository.getUserEvents()
    }
}
