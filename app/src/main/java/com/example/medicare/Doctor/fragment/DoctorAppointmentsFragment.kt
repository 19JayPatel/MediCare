package com.example.medicare.Doctor.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicare.Doctor.adapter.DoctorAppointmentsAdapter
import com.example.medicare.Doctor.model.AppointmentModel
import com.example.medicare.databinding.FragmentDoctorAppointmentsBinding

class DoctorAppointmentsFragment : Fragment() {

    private var _binding: FragmentDoctorAppointmentsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorAppointmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        val dummyAppointments = listOf(
            AppointmentModel("1", "Ravi Sharma", "Jan 15, 2025", "09:00 AM", "Skin allergy & rash on arms", "Pending"),
            AppointmentModel("2", "Meena Patel", "Jan 15, 2025", "10:30 AM", "Acne treatment follow-up", "Accepted"),
            AppointmentModel("3", "Arjun Desai", "Jan 16, 2025", "11:00 AM", "Eczema — recurring patches", "Pending"),
            AppointmentModel("4", "Sunita Rao", "Jan 17, 2025", "12:00 PM", "Psoriasis consultation", "Rejected"),
            AppointmentModel("5", "Vikram Singh", "Jan 18, 2025", "04:30 PM", "Routine skin check", "Pending")
        )

        binding.rvAppointments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = DoctorAppointmentsAdapter(dummyAppointments)
        }
    }

    private fun setupClickListeners() {
        binding.btnFilter.setOnClickListener {
            Toast.makeText(requireContext(), "Filter clicked", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}