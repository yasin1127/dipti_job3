package com.example.locationsharingapp_dipti_15.viewmodel15

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.locationsharingapp_dipti_15.model15.User15
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreViewModel15 : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")

    fun saveUser(
        context: Context,
        userId: String,
        displayName: String,
        email: String,
        location: String
    ) {
        val user = hashMapOf(
            "displayName" to displayName,
            "email" to email,
            "location" to location
        )

        usersCollection.document(userId).set(user)
            .addOnSuccessListener {
                Toast.makeText(
                    context,
                    "User saved successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    fun getAllUsers(context: Context, callback: (List<User15>) -> Unit) {
        usersCollection.get()
            .addOnSuccessListener {
                val userList = mutableListOf<User15>()
                for (document in it) {
                    val userId = document.id
                    val displayName = document.getString("displayName") ?: ""
                    val email = document.getString("email") ?: ""
                    val location = document.getString("location") ?: ""
                    userList.add(User15(userId, displayName, email, location))
                }
                callback(userList)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    fun updateUser(
        context: Context,
        userId: String,
        displayName: String,
        location: String
    ) {
        val user = hashMapOf(
            "displayName" to displayName,
            "location" to location
        )

        val userMap = user.toMap()
        usersCollection.document(userId).update(userMap)
            .addOnSuccessListener {
                Toast.makeText(
                    context,
                    "User updated successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    fun updateUserLocation(context: Context, userId: String, location: String) {
        if (userId.isEmpty()) {
            return
        }

        val user = hashMapOf("location" to location)
        val userMap = user.toMap()

        usersCollection.document(userId).update(userMap)
            .addOnSuccessListener {
                Toast.makeText(
                    context,
                    "User location updated successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    fun getUser(context: Context, userId: String, callback: (User15?) -> Unit) {
        usersCollection.document(userId).get()
            .addOnSuccessListener {
                val user = it.toObject(User15::class.java)
                callback(user)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
                callback(null)
            }
    }

    fun getUserLocation(context: Context, userId: String, callback: (String) -> Unit) {
        usersCollection.document(userId).get()
            .addOnSuccessListener {
                val location = it.getString("location") ?: ""
                callback(location)
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
                callback("")
            }
    }
}
