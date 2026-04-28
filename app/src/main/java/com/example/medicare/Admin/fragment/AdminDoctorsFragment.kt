package com.example.medicare.Admin.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private val filteredList = mutableListOf<AdminDoctorModel>()
    private lateinit var adapter: AdminDoctorsAdapter
    
    // To store schedules temporarily to build the summary
    private val doctorSchedulesMap = mutableMapOf<String, String>()

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
        setupSearch()
        fetchSchedulesAndDoctors()
        
        binding.btnAddDoctor.setOnClickListener {
            // Existing navigation for adding doctor
            // findNavController().navigate(R.id.action_adminDoctorsFragment_to_addDoctorActivity)
        }
    }

    private fun setupRecyclerView() {
        adapter = AdminDoctorsAdapter(filteredList)
        binding.rvDoctors.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDoctors.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterDoctors(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterDoctors(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(doctorList)
        } else {
            val lowerCaseQuery = query.lowercase()
            for (doctor in doctorList) {
                if (doctor.name.lowercase().contains(lowerCaseQuery) ||
                    doctor.specialization.lowercase().contains(lowerCaseQuery) ||
                    doctor.hospital.lowercase().contains(lowerCaseQuery)
                ) {
                    filteredList.add(doctor)
                }
            }
        }
        binding.tvLabel.text = "ALL DOCTORS (${filteredList.size})"
        adapter.updateList(filteredList)
    }

    private fun fetchSchedulesAndDoctors() {
        // First fetch all schedules to create summaries
        database.child("DoctorSchedules").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(schedulesSnapshot: DataSnapshot) {
                doctorSchedulesMap.clear()
                for (docScheduleSnap in schedulesSnapshot.children) {
                    val doctorId = docScheduleSnap.key ?: continue
                    val schedules = mutableListOf<DataSnapshot>()
                    for (scheduleSnap in docScheduleSnap.children) {
                        schedules.add(scheduleSnap)
                    }
                    
                    if (schedules.isNotEmpty()) {
                        // Example: "Mon–Fri · 9AM–5PM" or "Multiple Days"
                        // Simple logic: get days and timing from first entry or range
                        val first = schedules.first()
                        val day = first.child("day").value as? String ?: ""
                        val start = first.child("startTime").value as? String ?: ""
                        val end = first.child("endTime").value as? String ?: ""
                        
                        val summary = if (schedules.size > 1) {
                            val last = schedules.last()
                            val lastDay = last.child("day").value as? String ?: ""
                            "${day.take(3)}–${lastDay.take(3)} · $start–$end"
                        } else {
                            "$day · $start–$end"
                        }
                        doctorSchedulesMap[doctorId] = summary
                    } else {
                        doctorSchedulesMap[doctorId] = "No schedule set"
                    }
                }
                // After schedules are loaded (or updated), fetch doctors
                fetchDoctors()
            }

            override fun onCancelled(error: DatabaseError) {
                fetchDoctors() // Fallback
            }
        })
    }

    private fun fetchDoctors() {
        database.child("Doctors").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding == null) return
                doctorList.clear()
                for (postSnapshot in snapshot.children) {
                    val id = postSnapshot.key ?: ""
                    val name = postSnapshot.child("fullName").value as? String ?: ""
                    val specialty = postSnapshot.child("specialty").value as? String ?: ""
                    val hospital = postSnapshot.child("hospital").value as? String ?: ""
                    val status = postSnapshot.child("status").value as? String ?: "Active"
                    val imageUrl = postSnapshot.child("imageUrl").value as? String ?: ""
                    
                    val timingSummary = doctorSchedulesMap[id] ?: "Not Available"
                    
                    val doctor = AdminDoctorModel(
                        id = id,
                        name = "Dr. $name",
                        specialization = specialty,
                        hospital = hospital,
                        timing = timingSummary,
                        status = status,
                        imageUrl = imageUrl
                    )
                    doctorList.add(doctor)
                }
                
                filterDoctors(binding.etSearch.text.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AdminDoctors", "Error: ${error.message}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
