package com.app.kayipetapp.domain.use_case

import android.util.Log
import com.app.kayipetapp.common.Resource
import com.app.kayipetapp.domain.models.Event
import com.app.kayipetapp.domain.repository.FirebaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class AddEventsUseCase  @Inject constructor(private  val repository: FirebaseRepository) {
    suspend operator fun invoke(event: Event): Flow<Resource<Unit>> {
        return repository.addEvent(event)
    }
}