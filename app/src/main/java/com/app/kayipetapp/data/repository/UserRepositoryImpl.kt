package com.app.kayipetapp.data.repository

import android.util.Log
import com.app.kayipetapp.common.FirebasePaths
import com.app.kayipetapp.common.Resource
import com.app.kayipetapp.domain.models.Event
import com.app.kayipetapp.domain.models.UserModel
import com.app.kayipetapp.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val database: DatabaseReference,
    private val storageReference: StorageReference
) : UserRepository {

    override suspend fun getUserProfile(): Flow<Resource<MutableList<UserModel>>> = callbackFlow {
        try {
            trySend(Resource.Loading())

            val userId = firebaseAuth.currentUser?.uid

            if (userId != null) {
                val userRef = database.child(FirebasePaths.USERS).child(userId)
                userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(UserModel::class.java)
                        if (user != null) {
                            val userList = mutableListOf<UserModel>()
                            userList.add(user)
                            trySend(Resource.Success(userList))
                        } else {
                            trySend(Resource.Error(message = "User profile not found"))
                        }
                        close()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        trySend(Resource.Error(message = error.message))
                        close()
                    }
                })
            } else {
                trySend(Resource.Error(message = "User is not authenticated"))
                close()
            }
        } catch (e: Exception) {
            trySend(Resource.Error(message = "An error occurred: ${e.message}"))
        }

        awaitClose()
    }

    override suspend fun deleteUserProfile(userModel: UserModel): Flow<Resource<Unit>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserNick(newNick: String): Flow<Resource<Unit>> = callbackFlow {
        try {
            trySend(Resource.Loading())

            val currentUserID = firebaseAuth.currentUser?.uid

            if (currentUserID != null) {
                val userRef = database.child(FirebasePaths.USERS).child(currentUserID)

                userRef.child("userNick").setValue(newNick)
                    .addOnSuccessListener {
                        trySend(Resource.Success(Unit))
                    }
                    .addOnFailureListener { exception ->
                        trySend(Resource.Error(message = exception.message ?: "Failed to update user nickname"))
                    }
            } else {
                trySend(Resource.Error(message = "Current user email not found"))
            }
        } catch (e: Exception) {
            trySend(Resource.Error(message = "An error occurred: ${e.message}"))
        }

        awaitClose()
    }

    override suspend fun getUserEvents(): Flow<Resource<MutableList<Event>>> = callbackFlow {
        try {
            trySend(Resource.Loading())

            val userId = firebaseAuth.currentUser?.uid
            if (userId != null) {
                val userEventsRef = database.child(FirebasePaths.USERS).child(userId).child(FirebasePaths.USER_EVENTS)
                userEventsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val events = mutableListOf<Event>()
                        snapshot.children.forEach { eventSnapshot ->
                            val event = eventSnapshot.getValue(Event::class.java)
                            event?.let {
                                events.add(it)
                            }
                        }
                        trySend(Resource.Success(events))
                        close()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        trySend(Resource.Error(message = error.message))
                        close()
                    }
                })
            } else {
                trySend(Resource.Error(message = "User is not authenticated"))
                close()
            }
        } catch (e: Exception) {
            trySend(Resource.Error(message = "An error occurred: ${e.message}"))
        }

        awaitClose()
    }

    override suspend fun deleteUserEvent(eventID: String): Flow<Resource<Unit>> = callbackFlow {
        val currentUserID = firebaseAuth.currentUser?.uid
        val eventsRef = FirebaseDatabase.getInstance().getReference("users/$currentUserID/${FirebasePaths.USER_EVENTS}")

        try {
            eventsRef.orderByChild("eventID").equalTo(eventID).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val eventSnapshot = snapshot.children.first()
                        eventSnapshot.ref.removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                deleteImages(eventID, currentUserID!!)
                                trySend(Resource.Success(Unit))
                                close()
                            } else {
                                trySend(Resource.Error(task.exception?.message ?: "An error occurred", null))
                                close()
                            }
                        }
                    } else {
                        trySend(Resource.Error("Event with ID $eventID not found", null))
                        close()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(Resource.Error(error.message, null))
                    close()
                }
            })
        } catch (e: Exception) {
            trySend(Resource.Error(e.message ?: "An error occurred", null))
            close()
        }

        awaitClose()
    }

    private fun deleteImages(eventID: String, currentUserID: String) {
        val storageReference = FirebaseStorage.getInstance().getReference("$currentUserID/${FirebasePaths.USER_EVENTS}/$eventID/${FirebasePaths.IMAGES}/")

        storageReference.listAll().addOnSuccessListener { result ->
            result.items.forEach { imageReference ->
                imageReference.delete().addOnFailureListener { exception ->
                    Log.e("DeleteImageError", "Error deleting image: ${exception.message}")
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("DeleteImagesError", "Error deleting images: ${exception.message}")
        }
    }
}
