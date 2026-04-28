package com.example.medicare.Doctor.fragment

import android.content.Intent
import android.os.Bundle

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.medicare.R
import com.example.medicare.databinding.FragmentDoctorProfileBinding
import com.example.medicare.databinding.ItemProfileOptionBinding
import com.example.medicare.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DoctorProfileFragment : Fragment() {

    private var _binding: FragmentDoctorProfileBinding? = null
    private val binding get() = _binding!!

    // 1. Firebase setup
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorProfileBinding.inflate(inflater, container, false)

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 2. Integration: Call the fetch logic
        fetchDoctorProfile()

        setupProfileOptions()
        setupClickListeners()
    }

    /**
     * 3. Fetch logic for Doctor Specialty from Firebase
     * Path: Doctors -> currentDoctorUid -> specialty
     */
    private fun fetchDoctorProfile() {
        // Get current logged-in user UID
        val userId = auth.currentUser?.uid ?: return

        // Reference to Doctors/{uid}
        database.child("Doctors").child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // 4. Lifecycle & Null Safety: Ensure binding is still available
                if (_binding == null) return

                if (snapshot.exists()) {
                    // 5. Fetch specialty from database
                    val specialty = snapshot.child("specialty").value as? String ?: "Specialist"

                    // 6. Loading specialty into tvRoleBadge
                    binding.tvRoleBadge.text = specialty

                    // Optional: Fetch other details if they exist in the same node
                    val name = snapshot.child("fullName").value as? String
                    if (name != null) binding.tvDoctorName.text = "Dr. $name"

                    val email = snapshot.child("email").value as? String
                    if (email != null) binding.tvDoctorEmail.text = email
                } else {
                    Log.d("DoctorProfile", "No doctor record found for UID: $userId")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // 7. Error handling
                Log.e("DoctorProfile", "Database error: ${error.message}")
            }
        })
    }

    private fun setupProfileOptions() {
        // ... (Keep your existing UI setup code)
        setupOption(
            binding.optionEditProfile,
            "Edit Profile",
            "Update your details",
            R.drawable.ic_person
        )
        setupOption(
            binding.optionAvailability,
            "Availability",
            "Set working hours",
            R.drawable.ic_time
        )
        setupOption(
            binding.optionNotifications,
            "Notifications",
            "Manage alerts",
            R.drawable.ic_notification
        )
        setupOption(binding.optionSecurity, "Security", "Password & access", R.drawable.ic_lock)
        setupOption(
            binding.optionAppearance,
            "Appearance",
            "Theme & display",
            R.drawable.ic_settings
        )
        setupOption(binding.optionHelp, "Help & Support", "FAQs and contact", R.drawable.ic_help)
    }

    private fun setupOption(
        binding: ItemProfileOptionBinding,
        title: String,
        subtitle: String,
        iconRes: Int
    ) {
        binding.tvOptionTitle.text = title
        binding.tvOptionSubtitle.text = subtitle
        binding.ivOptionIcon.setImageResource(iconRes)
    }

    private fun setupClickListeners() {
        binding.btnLogout.setOnClickListener { logout() }
        binding.btnEditTop.setOnClickListener {
            Toast.makeText(requireContext(), "Edit Profile", Toast.LENGTH_SHORT).show()
        }
    }

    private fun logout() {
        auth.signOut()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}