package com.app.kayipetapp.domain.use_case

import com.app.kayipetapp.common.Resource
import com.app.kayipetapp.domain.models.Event
import com.app.kayipetapp.domain.repository.FirebaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEventsUseCase  @Inject constructor(private  val repository: FirebaseRepository) {
    suspend operator fun invoke(): Flow<Resource<MutableList<Event>>> {
        return repository.getEvents()
    }
}

