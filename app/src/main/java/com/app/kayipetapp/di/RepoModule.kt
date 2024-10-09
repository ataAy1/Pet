package com.app.kayipetapp.di

import com.app.kayipetapp.data.repository.AuthRepositoryImpl
import com.app.kayipetapp.data.repository.FirebaseRepositoryImpl
import com.app.kayipetapp.data.repository.UserRepositoryImpl
import com.app.kayipetapp.domain.repository.AuthRepository
import com.app.kayipetapp.domain.repository.FirebaseRepository
import com.app.kayipetapp.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideFirebaseRepository(
        firebaseAuth: FirebaseAuth,
        database: DatabaseReference,
        storageReference: StorageReference
    ): FirebaseRepository {
        return FirebaseRepositoryImpl(firebaseAuth, database,storageReference)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        database: DatabaseReference
    ): AuthRepository {
        return AuthRepositoryImpl(firebaseAuth, database)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        firebaseAuth: FirebaseAuth,
        database: DatabaseReference,
        storageReference: StorageReference
    ): UserRepository {
        return UserRepositoryImpl(firebaseAuth, database,storageReference)
    }

}