package com.app.kayipetapp.data.repository

import android.net.Uri
import android.util.Log
import com.app.kayipetapp.common.FirebasePaths
import com.app.kayipetapp.common.Resource
import com.app.kayipetapp.domain.models.DateTime
import com.app.kayipetapp.domain.models.Event
import com.app.kayipetapp.domain.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.tasks.await
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.channels.awaitClose
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val database: DatabaseReference,
    private val storageReference: StorageReference
) : FirebaseRepository {

    override suspend fun addEvent(event: Event): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())

        val currentUser = firebaseAuth.currentUser ?: run {
            emit(Resource.Error(message = "User not authenticated"))
            return@flow
        }

        try {
            val userId = currentUser.uid
            val eventsRef = database.child(FirebasePaths.EVENT).child(userId).child(FirebasePaths.USER_EVENTS)

            val newEventRef = eventsRef.push()
            val eventId = newEventRef.key ?: event.eventID
            val imageUrls = uploadImages(event.images, currentUser.uid, eventId)

            val eventWithImages = event.copy(images = imageUrls)

            newEventRef.setValue(eventWithImages).await()
            emit(Resource.Success(Unit))

        } catch (e: Exception) {
            emit(Resource.Error(message = "An error occurred: ${e.message}"))
        }
    }

    private suspend fun uploadImages(images: HashMap<String, String>?, userId: String, eventId: String): HashMap<String, String> {
        val imageUrls = hashMapOf<String, String>()
        images?.forEach { (index, imageUrl) ->
            val imageRef = storageReference.child("$userId/${FirebasePaths.USER_EVENTS}/$eventId/${FirebasePaths.IMAGES}/$index.jpg")
            val uploadTask = imageRef.putFile(Uri.parse(imageUrl))
            uploadTask.await()
            val downloadUrl = imageRef.downloadUrl.await()
            imageUrls[index] = downloadUrl.toString()
        }
        return imageUrls
    }

    override suspend fun getEvents(): Flow<Resource<MutableList<Event>>> = callbackFlow {
        trySend(Resource.Loading())

        val usersRef = database.child(FirebasePaths.EVENT)
        val usersListener = object : ChildEventListener {
            override fun onChildAdded(userSnapshot: DataSnapshot, previousChildName: String?) {
                val eventsList = mutableListOf<Event>()
                userSnapshot.child("events").children.forEach { eventSnapshot ->
                    val event = parseEvent(eventSnapshot)
                    eventsList.add(event)
                }
                trySend(Resource.Success(data = eventsList))
            }

            override fun onChildChanged(userSnapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(userSnapshot: DataSnapshot) {
            }

            override fun onChildMoved(userSnapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(databaseError: DatabaseError) {
                trySend(Resource.Error(message = "Failed to retrieve events: $databaseError"))
            }
        }

        usersRef.addChildEventListener(usersListener)

        awaitClose { usersRef.removeEventListener(usersListener) }
    }.catch { e ->
        emit(Resource.Error(message = "An error occurred: ${e.message}"))
    }

    private fun parseEvent(eventSnapshot: DataSnapshot): Event {
        val latitude = eventSnapshot.child("latitude").getValue(Double::class.java) ?: 0.0
        val longitude = eventSnapshot.child("longitude").getValue(Double::class.java) ?: 0.0
        val eventType = eventSnapshot.child("eventType").getValue(String::class.java) ?: ""
        val address = eventSnapshot.child("address").getValue(String::class.java) ?: ""
        val eventDateTime = eventSnapshot.child("dateTime").getValue(DateTime::class.java) ?: DateTime()
        val description = eventSnapshot.child("description").getValue(String::class.java) ?: ""
        val userName = eventSnapshot.child("userName").getValue(String::class.java) ?: ""
        val images = eventSnapshot.child("images").value as? HashMap<String, String>
        val eventId = eventSnapshot.child("eventID").getValue(String::class.java) ?: ""

        return Event(
            latitude = latitude,
            longitude = longitude,
            description = description,
            userName = userName,
            images = images,
            dateTime = eventDateTime,
            adress = address,
            eventType = eventType,
            eventID = eventId
        )
    }
}
