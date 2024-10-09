package com.app.kayipetapp.domain.repository

import com.app.kayipetapp.common.Resource
import com.app.kayipetapp.domain.models.Event
import com.app.kayipetapp.domain.models.UserModel
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserProfile(): Flow<Resource<MutableList<UserModel>>>

    suspend fun deleteUserProfile(userModel: UserModel): Flow<Resource<Unit>>

    suspend fun updateUserNick(newUserNick: String): Flow<Resource<Unit>>

    suspend fun getUserEvents(): Flow<Resource<MutableList<Event>>>

    suspend fun deleteUserEvent(eventID: String): Flow<Resource<Unit>>


}