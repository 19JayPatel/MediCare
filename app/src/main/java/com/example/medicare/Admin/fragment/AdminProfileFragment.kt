package com.example.medicare.Admin.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.medicare.databinding.FragmentAdminProfileBinding

class AdminProfileFragment : Fragment() {

    private var _binding: FragmentAdminProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()
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
            showToast("Logging Out...")
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
