package com.example.medicare.Admin.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicare.Admin.adapter.TopDoctorsAdapter
import com.example.medicare.Admin.model.DoctorModel
import com.example.medicare.R
import com.example.medicare.databinding.FragmentAdminDashboardBinding

class AdminDashboardFragment : Fragment() {

    private var _binding: FragmentAdminDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTopDoctorsRecyclerView()
    }

    private fun setupTopDoctorsRecyclerView() {
        val doctors = listOf(
            DoctorModel("Dr. Sarah Johnson", "Cardiologist", "Apollo Hospital", rating = 4.9, imageRes = R.drawable.ic_doctor1),
            DoctorModel("Dr. Rahul Mehta", "Neurologist", "Fortis Hospital", rating = 4.8, imageRes = R.drawable.ic_doctor2),
            DoctorModel("Dr. Priya Nair", "Dermatologist", "AIIMS Delhi", rating = 4.7, imageRes = R.drawable.ic_doctor3),
            DoctorModel("Dr. Amit Verma", "Pediatrician", "Max Healthcare", rating = 4.9, imageRes = R.drawable.ic_doctor4)
        )

        val adapter = TopDoctorsAdapter(doctors)
        binding.rvTopDoctors.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTopDoctors.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
