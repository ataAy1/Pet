package com.app.kayipetapp.data.repository

import com.app.kayipetapp.common.FirebasePaths
import com.app.kayipetapp.common.Resource
import com.app.kayipetapp.domain.models.UserModel
import com.app.kayipetapp.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val database: DatabaseReference
) : AuthRepository {

    override fun createUser(userModel: UserModel): Flow<Resource<UserModel>> = callbackFlow {
        firebaseAuth.createUserWithEmailAndPassword(userModel.email, userModel.password.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val uid = user?.uid
                    uid?.let { userId ->
                        val userMap = mapOf(
                            "userId" to userId,
                            "email" to userModel.email,
                            "userNick" to userModel.userNick
                        )
                        database.child(FirebasePaths.USERS).child(userId).setValue(userMap)
                            .addOnSuccessListener {
                                trySend(Resource.Success(userModel))
                                close()
                            }
                            .addOnFailureListener { e ->
                                trySend(Resource.Error(message = e.message ?: "Failed to store user data"))
                                close()
                            }
                    } ?: run {
                        trySend(Resource.Error(message = "Failed to retrieve user ID"))
                        close()
                    }
                } else {
                    trySend(Resource.Error(message = task.exception?.message ?: "Registration failed"))
                    close()
                }
            }

        awaitClose {
        }
    }.catch { e ->
        emit(Resource.Error(message = "An error occurred: ${e.message}"))
    }

    override fun signIn(email: String, password: String): Flow<Resource<Unit>> = callbackFlow {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    trySend(Resource.Success(Unit))
                    close()
                } else {
                    trySend(Resource.Error(message = task.exception?.message ?: "Sign-in failed"))
                    close()
                }
            }

        awaitClose {
        }
    }.catch { e ->
        emit(Resource.Error(message = "An error occurred: ${e.message}"))
    }

    override fun signOut(): Flow<Resource<Unit>> = flow {
        try {
            firebaseAuth.signOut()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(message = "An error occurred: ${e.message}"))
        }
    }

    override fun resetPassword(email: String): Flow<Resource<Unit>> = callbackFlow {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    trySend(Resource.Success(Unit))
                    close()
                } else {
                    trySend(Resource.Error(message = task.exception?.message ?: "Reset password failed"))
                    close()
                }
            }

        awaitClose {
        }
    }.catch { e ->
        emit(Resource.Error(message = "An error occurred: ${e.message}"))
    }

    override fun deleteAccount(): Flow<Resource<Unit>> = callbackFlow {
        val currentUser = firebaseAuth.currentUser

        if (currentUser == null) {
            trySend(Resource.Error(message = "User is not authenticated"))
            close()
            return@callbackFlow
        }

        currentUser.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    trySend(Resource.Success(Unit))
                } else {
                    trySend(Resource.Error(message = task.exception?.message ?: "Account deletion failed"))
                }
                close()
            }

        awaitClose {
        }
    }.catch { e ->
        emit(Resource.Error(message = "An error occurred: ${e.message}"))
    }
}
