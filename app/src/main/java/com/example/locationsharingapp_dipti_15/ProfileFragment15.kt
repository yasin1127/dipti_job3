package com.example.locationsharingapp_dipti_15

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.locationsharingapp_dipti_15.databinding.FragmentProfile15Binding
import com.example.locationsharingapp_dipti_15.view15.LoginActivity15
import com.example.locationsharingapp_dipti_15.view15.MainActivity15
import com.example.locationsharingapp_dipti_15.viewmodel15.AuthenticationViewModel15
import com.example.locationsharingapp_dipti_15.viewmodel15.FirestoreViewModel15
import com.example.locationsharingapp_dipti_15.viewmodel15.LocationViewModel15
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment15 : Fragment() {

    private lateinit var binding: FragmentProfile15Binding
    private lateinit var authViewModel: AuthenticationViewModel15
    private lateinit var firestoreViewModel: FirestoreViewModel15
    private lateinit var locationViewModel: LocationViewModel15
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfile15Binding.inflate(inflater, container, false)
        authViewModel = ViewModelProvider(this).get(AuthenticationViewModel15::class.java)
        firestoreViewModel = ViewModelProvider(this).get(FirestoreViewModel15::class.java)
        locationViewModel = ViewModelProvider(this).get(LocationViewModel15::class.java)

        binding.logoutBtn15.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(requireContext(), LoginActivity15::class.java))
        }
        binding.homeBtn15.setOnClickListener {
            startActivity(Intent(requireContext(), MainActivity15::class.java))
        }
        loadUserInfo()

        binding.updateBtn15.setOnClickListener {
            val newName = binding.nameEt.text.toString()
            val newLocation = binding.locationEt.text.toString()

            updateProfile(newName, newLocation)
        }

        return binding.root
    }

    private fun updateProfile(newName: String, newLocation: String) {
        val currentUser = authViewModel.getCurrentUser()
        if (currentUser != null) {
            val userId = currentUser.uid
            firestoreViewModel.updateUser(requireContext(), userId, newName, newLocation)
            Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
            startActivity(Intent(requireContext(), MainActivity15::class.java))
        } else {
            // Handle the case where the current user is null
            Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUserInfo() {
        val currentUser = authViewModel.getCurrentUser()
        if (currentUser != null) {
            binding.emailEt.setText(currentUser.email)

            firestoreViewModel.getUser(requireContext(), currentUser.uid) {
                if (it != null) {
                    binding.nameEt.setText(it.displayName)

                    firestoreViewModel.getUserLocation(requireContext(), currentUser.uid) {
                        if (it.isNotEmpty()) {
                            binding.locationEt.setText(it)
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
        }
    }
}
