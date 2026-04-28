package com.example.medicare.Admin.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicare.Admin.adapter.AdminDoctorsAdapter
import com.example.medicare.Admin.model.AdminDoctorModel
import com.example.medicare.R
import com.example.medicare.databinding.FragmentAdminDoctorsBinding
import com.google.firebase.database.*

class AdminDoctorsFragment : Fragment() {

    private var _binding: FragmentAdminDoctorsBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private val doctorList = mutableListOf<AdminDoctorModel>()
    private lateinit var adapter: AdminDoctorsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminDoctorsBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance().reference
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        fetchDoctors()
    }

    private fun setupRecyclerView() {
        adapter = AdminDoctorsAdapter(doctorList)
        binding.rvDoctors.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDoctors.adapter = adapter
    }

    private fun fetchDoctors() {
        database.child("doctors").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                doctorList.clear()
                for (postSnapshot in snapshot.children) {
                    val doctor = postSnapshot.getValue(AdminDoctorModel::class.java)
                    doctor?.let { 
                        val docWithId = it.copy(id = postSnapshot.key ?: it.id)
                        doctorList.add(docWithId) 
                    }
                }
                
                if (doctorList.isEmpty()) {
                    // Fallback to static data
                    doctorList.addAll(listOf(
                        AdminDoctorModel("1", "Dr. Sarah Johnson", "Cardiologist", "Apollo Hospital", "Mon–Sat · 9AM–5PM", "Active", R.drawable.ic_doctor1),
                        AdminDoctorModel("2", "Dr. Rahul Mehta", "Neurologist", "Fortis Hospital", "Mon–Fri · 10AM–6PM", "Active", R.drawable.ic_doctor2),
                        AdminDoctorModel("3", "Dr. Priya Nair", "Dermatologist", "AIIMS Delhi", "Tue–Sat · 11AM–4PM", "Active", R.drawable.ic_doctor3)
                    ))
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load doctors", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
