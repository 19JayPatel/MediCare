package com.example.medicare.Doctor.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.medicare.R
import com.example.medicare.databinding.FragmentDoctorProfileBinding
import com.example.medicare.databinding.ItemProfileOptionBinding

class DoctorProfileFragment : Fragment() {

    private var _binding: FragmentDoctorProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupProfileOptions()
        setupClickListeners()
    }

    private fun setupProfileOptions() {
        // Setup Account Section
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

        setupOption(
            binding.optionSecurity,
            "Security",
            "Password & access",
            R.drawable.ic_lock
        )

        // Setup Preferences Section
        setupOption(
            binding.optionAppearance,
            "Appearance",
            "Theme & display",
            R.drawable.ic_settings
        )

        setupOption(
            binding.optionHelp,
            "Help & Support",
            "FAQs and contact",
            R.drawable.ic_help
        )
    }

    private fun setupOption(binding: ItemProfileOptionBinding, title: String, subtitle: String, iconRes: Int) {
        binding.tvOptionTitle.text = title
        binding.tvOptionSubtitle.text = subtitle
        binding.ivOptionIcon.setImageResource(iconRes)
    }

    private fun setupClickListeners() {
        binding.btnLogout.setOnClickListener {
            Toast.makeText(requireContext(), "Logging out...", Toast.LENGTH_SHORT).show()
        }

        binding.btnEditTop.setOnClickListener {
            Toast.makeText(requireContext(), "Edit Profile", Toast.LENGTH_SHORT).show()
        }
        
        binding.optionEditProfile.root.setOnClickListener {
            Toast.makeText(requireContext(), "Edit Profile clicked", Toast.LENGTH_SHORT).show()
        }
        
        binding.optionAvailability.root.setOnClickListener {
            Toast.makeText(requireContext(), "Availability clicked", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
