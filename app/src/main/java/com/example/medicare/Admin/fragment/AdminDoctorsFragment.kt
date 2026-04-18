package com.example.medicare.Admin.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicare.Admin.adapter.AdminDoctorsAdapter
import com.example.medicare.Admin.model.AdminDoctorModel
import com.example.medicare.R
import com.example.medicare.databinding.FragmentAdminDoctorsBinding

class AdminDoctorsFragment : Fragment() {

    private var _binding: FragmentAdminDoctorsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminDoctorsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val doctors = listOf(
            AdminDoctorModel("1", "Dr. Sarah Johnson", "Cardiologist", "Apollo Hospital", "Mon–Sat · 9AM–5PM", "Active", R.drawable.ic_doctor1),
            AdminDoctorModel("2", "Dr. Rahul Mehta", "Neurologist", "Fortis Hospital", "Mon–Fri · 10AM–6PM", "Active", R.drawable.ic_doctor2),
            AdminDoctorModel("3", "Dr. Priya Nair", "Dermatologist", "AIIMS Delhi", "Tue–Sat · 11AM–4PM", "Active", R.drawable.ic_doctor3),
            AdminDoctorModel("4", "Dr. Anil Kumar", "Orthopedic", "Max Hospital", "Mon–Wed · 9AM–2PM", "On Leave", R.drawable.ic_doctor4),
            AdminDoctorModel("5", "Dr. Neha Sharma", "Pediatrician", "Rainbow Hospital", "Mon–Sat · 10AM–5PM", "Active", R.drawable.ic_doctor1),
            AdminDoctorModel("6", "Dr. Vikram Singh", "Psychiatrist", "Medanta", "Wed–Sun · 2PM–8PM", "Active", R.drawable.ic_doctor2)
        )

        binding.rvDoctors.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDoctors.adapter = AdminDoctorsAdapter(doctors)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
