package com.example.medicare.Doctor.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicare.Doctor.adapter.DashboardAppointmentsAdapter
import com.example.medicare.Doctor.model.AppointmentModel
import com.example.medicare.R
import com.example.medicare.databinding.FragmentDoctorDashboardBinding

class DoctorDashboardFragment : Fragment() {

    private var _binding: FragmentDoctorDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        // Data logic: Dashboard shows only Accepted appointments
        val fullList = listOf(
            AppointmentModel("1", "Ravi Sharma", "Today", "09:00 AM", "Skin Allergy", "Accepted"),
            AppointmentModel("2", "Meena Patel", "Today", "10:30 AM", "Acne Treatment", "Pending"),
            AppointmentModel("3", "Rahul Verma", "Today", "11:45 AM", "Checkup", "Accepted"),
            AppointmentModel("4", "Sonia Gill", "Today", "02:00 PM", "Follow-up", "Pending")
        )

        val acceptedList = fullList.filter { it.status == "Accepted" }

        binding.rvTodayAppointments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            // Using specific adapter for Dashboard
            adapter = DashboardAppointmentsAdapter(acceptedList)
            isNestedScrollingEnabled = false
        }
    }

    private fun setupClickListeners() {
        binding.tvSeeAll.setOnClickListener {
            Toast.makeText(requireContext(), "View all appointments", Toast.LENGTH_SHORT).show()
        }

        binding.ivDoctorProfile.setOnClickListener {
            Toast.makeText(requireContext(), "Profile clicked", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}