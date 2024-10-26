package com.example.locationsharingapp_dipti_15

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.locationsharingapp_dipti_15.adapter15.UserAdaper15
import com.example.locationsharingapp_dipti_15.databinding.FragmentFriends15Binding
import com.example.locationsharingapp_dipti_15.view15.MapsActivity15
import com.example.locationsharingapp_dipti_15.viewmodel15.AuthenticationViewModel15
import com.example.locationsharingapp_dipti_15.viewmodel15.FirestoreViewModel15
import com.example.locationsharingapp_dipti_15.viewmodel15.LocationViewModel15
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class FriendsFragment : Fragment() {
    private lateinit var binding: FragmentFriends15Binding
    private lateinit var firestoreViewModel: FirestoreViewModel15
    private lateinit var authenticationViewModel: AuthenticationViewModel15
    private lateinit var userAdapter: UserAdaper15
    private lateinit var locationViewModel: LocationViewModel15
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getLocation()
            } else {
                Toast.makeText(requireContext(), "Location Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriends15Binding.inflate(inflater, container, false)

        firestoreViewModel = ViewModelProvider(this).get(FirestoreViewModel15::class.java)
        locationViewModel = ViewModelProvider(this).get(LocationViewModel15::class.java)
        authenticationViewModel = ViewModelProvider(this).get(AuthenticationViewModel15::class.java)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationViewModel.initializeFusedLocationClient(fusedLocationClient)

        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request the permission
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            // Permission is already granted
            getLocation()
        }

        userAdapter = UserAdaper15(emptyList())
        binding.userRV.apply {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        fetchUsers()

        binding.locationBtn.setOnClickListener {
            startActivity(Intent(requireContext(), MapsActivity15::class.java))
        }

        return binding.root
    }

    private fun fetchUsers() {
        firestoreViewModel.getAllUsers(requireContext()) {
            userAdapter.updateData(it)
        }
    }

    private fun getLocation() {
        locationViewModel.getLastLocation {
            // Save location to Firestore for the current user
            authenticationViewModel.getCurrentUserId().let { userId ->
                firestoreViewModel.updateUserLocation(requireContext(), userId, it)
            }
        }
    }
}
