package com.app.kayipetapp.domain.repository

import com.app.kayipetapp.common.Resource
import com.app.kayipetapp.domain.models.Event
import kotlinx.coroutines.flow.Flow

interface FirebaseRepository {
    suspend fun getEvents(): Flow<Resource<MutableList<Event>>>
    suspend fun addEvent(event: Event): Flow<Resource<Unit>>
}