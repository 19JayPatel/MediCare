package com.example.medicare.Doctor.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.medicare.R
import com.example.medicare.databinding.FragmentDoctorProfileBinding
import com.example.medicare.databinding.ItemProfileOptionBinding
import com.example.medicare.auth.LoginActivity
import com.example.medicare.utils.LogoutHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DoctorProfileFragment : Fragment() {

    private var _binding: FragmentDoctorProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorProfileBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchDoctorProfile()
        setupProfileOptions()
        setupClickListeners()
    }

    private fun fetchDoctorProfile() {
        val userId = auth.currentUser?.uid ?: return

        database.child("Doctors").child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding == null) return

                if (snapshot.exists()) {
                    val specialty = snapshot.child("specialty").value as? String ?: "Specialist"
                    binding.tvRoleBadge.text = specialty

                    val name = snapshot.child("fullName").value as? String
                    if (name != null) binding.tvDoctorName.text = "Dr. $name"

                    val email = snapshot.child("email").value as? String
                    if (email != null) binding.tvDoctorEmail.text = email

                    // Fetch and load profile image using Glide
                    val imageUrl = snapshot.child("imageUrl").value as? String
                    if (imageUrl != null && imageUrl.isNotEmpty()) {
                        Glide.with(requireContext())
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_person)
                            .error(R.drawable.ic_person)
                            .into(binding.ivAvatar)
                    } else {
                        binding.ivAvatar.setImageResource(R.drawable.ic_person)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DoctorProfile", "Database error: ${error.message}")
            }
        })
    }

    private fun setupProfileOptions() {
        setupOption(binding.optionEditProfile, "Edit Profile", "Update your details", R.drawable.ic_person)
        setupOption(binding.optionAvailability, "Availability", "Set working hours", R.drawable.ic_time)
        setupOption(binding.optionNotifications, "Notifications", "Manage alerts", R.drawable.ic_notification)
        setupOption(binding.optionSecurity, "Security", "Password & access", R.drawable.ic_lock)
        setupOption(binding.optionAppearance, "Appearance", "Theme & display", R.drawable.ic_settings)
        setupOption(binding.optionHelp, "Help & Support", "FAQs and contact", R.drawable.ic_help)
    }

    private fun setupOption(binding: ItemProfileOptionBinding, title: String, subtitle: String, iconRes: Int) {
        binding.tvOptionTitle.text = title
        binding.tvOptionSubtitle.text = subtitle
        binding.ivOptionIcon.setImageResource(iconRes)
    }

    private fun setupClickListeners() {
        binding.btnLogout.setOnClickListener { 
            LogoutHelper.showLogoutDialog(requireContext())
        }
        binding.btnEditTop.setOnClickListener {
            Toast.makeText(requireContext(), "Edit Profile", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
