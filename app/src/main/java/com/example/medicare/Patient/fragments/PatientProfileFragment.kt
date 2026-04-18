package com.example.medicare.Patient.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicare.Patient.activities.EditProfileActivity
import com.example.medicare.Patient.activities.MainActivity
import com.example.medicare.Patient.adapters.ProfileMenuAdapter
import com.example.medicare.R
import com.example.medicare.databinding.FragmentPatientProfileBinding
import com.example.medicare.Patient.models.ProfileMenuItem

class PatientProfileFragment : Fragment() {

    private var _binding: FragmentPatientProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPatientProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()
        setupClickListeners()
    }

    private fun setupMenu() {
        val menuItems = listOf(
            ProfileMenuItem(1, getString(R.string.edit_profile), R.drawable.ic_edit) {
                startActivity(Intent(requireContext(), EditProfileActivity::class.java))
            },
            ProfileMenuItem(2, getString(R.string.favorite), R.drawable.ic_heart) {
                handleMenuClick(getString(R.string.favorite))
            },
            ProfileMenuItem(3, getString(R.string.notifications), R.drawable.ic_notification) {
                handleMenuClick(getString(R.string.notifications))
            },
            ProfileMenuItem(4, getString(R.string.settings), R.drawable.ic_settings) {
                handleMenuClick(getString(R.string.settings))
            },
            ProfileMenuItem(5, getString(R.string.help_and_support), R.drawable.ic_help) {
                handleMenuClick(getString(R.string.help_and_support))
            },
            ProfileMenuItem(6, getString(R.string.terms_and_conditions), R.drawable.ic_info) {
                handleMenuClick(getString(R.string.terms_and_conditions))
            },
            ProfileMenuItem(7, getString(R.string.log_out), R.drawable.ic_logout) {
                handleMenuClick(getString(R.string.log_out))
            }
        )

        binding.rvProfileMenu.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ProfileMenuAdapter(menuItems)
        }
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            (requireActivity() as? MainActivity)?.navigateToHome()
        }
        
        binding.btnEditImage.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }
    }

    private fun handleMenuClick(title: String) {
        Log.d("PatientProfile", "Clicked on: $title")
        Toast.makeText(requireContext(), "Clicked: $title", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
