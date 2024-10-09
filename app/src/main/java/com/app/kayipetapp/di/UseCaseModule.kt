package com.app.kayipetapp.di

import com.app.kayipetapp.domain.repository.AuthRepository
import com.app.kayipetapp.domain.repository.FirebaseRepository
import com.app.kayipetapp.domain.repository.UserRepository
import com.app.kayipetapp.domain.use_case.AddEventsUseCase
import com.app.kayipetapp.domain.use_case.DeleteUserEventUseCase
import com.app.kayipetapp.domain.use_case.GetEventsUseCase
import com.app.kayipetapp.domain.use_case.GetUserEventsUseCase
import com.app.kayipetapp.domain.use_case.GetUserProfileUseCase
import com.app.kayipetapp.domain.use_case.LoginUseCase
import com.app.kayipetapp.domain.use_case.RegisterUseCase
import com.app.kayipetapp.domain.use_case.ResetPasswordUseCase
import com.app.kayipetapp.domain.use_case.UpdateUserNickUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    fun provideGetEventsUseCase(repository: FirebaseRepository): GetEventsUseCase {
        return GetEventsUseCase(repository)
    }

    @Provides
    fun provideAddEventsUseCase(repository: FirebaseRepository): AddEventsUseCase {
        return AddEventsUseCase(repository)
    }

    @Provides
    fun provideLoginUseCase(authRepository: AuthRepository): LoginUseCase {
        return LoginUseCase(authRepository)
    }

    @Provides
    fun provideRegisterUseCase(authRepository: AuthRepository): RegisterUseCase {
        return RegisterUseCase(authRepository)
    }

    @Provides
    fun provideResetPasswordUseCase(authRepository: AuthRepository): ResetPasswordUseCase {
        return ResetPasswordUseCase(authRepository)
    }

    @Provides
    fun provideGetUserProfile(userRepository: UserRepository): GetUserProfileUseCase {
        return GetUserProfileUseCase(userRepository)
    }

    @Provides
    fun provideUpdateUserNick(userRepository: UserRepository): UpdateUserNickUseCase {
        return UpdateUserNickUseCase(userRepository)
    }

    @Provides
    fun provideUserEvents(userRepository: UserRepository): GetUserEventsUseCase {
        return GetUserEventsUseCase(userRepository)
    }

    @Provides
    fun provideDeleteUserEvents(userRepository: UserRepository): DeleteUserEventUseCase {
        return DeleteUserEventUseCase(userRepository)
    }

}