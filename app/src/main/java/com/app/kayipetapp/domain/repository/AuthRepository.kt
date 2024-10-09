package com.app.kayipetapp.domain.repository

import com.app.kayipetapp.common.Resource
import com.app.kayipetapp.domain.models.UserModel
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun createUser(userModel: UserModel): Flow<Resource<UserModel>>
    fun signIn(email: String, password: String): Flow<Resource<Unit>>
    fun signOut(): Flow<Resource<Unit>>
    fun resetPassword(email: String): Flow<Resource<Unit>>
    fun deleteAccount(): Flow<Resource<Unit>>
}