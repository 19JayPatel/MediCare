package com.example.medicare.Admin.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.medicare.R
import com.example.medicare.databinding.FragmentAdminProfileBinding
import com.example.medicare.utils.LogoutHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AdminProfileFragment : Fragment() {

    private var _binding: FragmentAdminProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminProfileBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchAdminProfile()
        fetchStats()
        setupClickListeners()
    }

    private fun fetchAdminProfile() {
        val uid = auth.currentUser?.uid ?: return
        database.child("users").child(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding == null) return
                
                val name = snapshot.child("fullName").value as? String ?: "Admin User"
                val email = snapshot.child("email").value as? String ?: "admin@medicare.com"
                val role = snapshot.child("role").value as? String ?: "Super Admin"
                val imageUrl = snapshot.child("imageUrl").value as? String

                binding.tvAdminName.text = name
                binding.tvAdminEmail.text = email
                binding.tvAdminRole.text = role

                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(requireContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_person)
                        .into(binding.ivAdminAvatar)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun fetchStats() {
        // Total Doctors
        database.child("Doctors").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding != null) {
                    binding.tvDoctorsCount.text = snapshot.childrenCount.toString()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        // Total Users (Patients)
        database.child("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding == null) return
                var patientCount = 0
                for (userSnap in snapshot.children) {
                    val role = userSnap.child("role").value as? String
                    if (role == "Patient") {
                        patientCount++
                    }
                }
                binding.tvUsersCount.text = patientCount.toString()
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        // Total Appointments
        database.child("Appointments").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding != null) {
                    binding.tvAppointmentsCount.text = snapshot.childrenCount.toString()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setupClickListeners() {
        binding.btnEditProfileTop.setOnClickListener {
            showToast("Edit Profile Clicked")
        }

        binding.btnEditProfile.setOnClickListener {
            showToast("Edit Profile Option Clicked")
        }

        binding.btnNotifications.setOnClickListener {
            showToast("Notifications Clicked")
        }

        binding.btnSecurity.setOnClickListener {
            showToast("Security Clicked")
        }

        binding.btnAppearance.setOnClickListener {
            showToast("Appearance Clicked")
        }

        binding.btnActivityLog.setOnClickListener {
            showToast("Activity Log Clicked")
        }

        binding.btnHelp.setOnClickListener {
            showToast("Help & Support Clicked")
        }

        binding.btnLogout.setOnClickListener {
            LogoutHelper.showLogoutDialog(requireContext())
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
