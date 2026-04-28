package com.example.medicare.Admin.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicare.Admin.adapter.TopDoctorsAdapter
import com.example.medicare.Admin.model.DoctorModel
import com.example.medicare.R
import com.example.medicare.databinding.FragmentAdminDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AdminDashboardFragment : Fragment() {

    private var _binding: FragmentAdminDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private val topDoctorsList = mutableListOf<DoctorModel>()
    private lateinit var adapter: TopDoctorsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminDashboardBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTopDoctorsRecyclerView()
        fetchStats()
        fetchTopDoctors()
    }

    private fun setupTopDoctorsRecyclerView() {
        adapter = TopDoctorsAdapter(topDoctorsList)
        binding.rvTopDoctors.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTopDoctors.adapter = adapter
    }

    private fun fetchStats() {
        // Fetch Total Bookings
        database.child("appointments").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding != null) {
                    binding.tvTotalBookingsCount.text = snapshot.childrenCount.toString()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        // Fetch Total Doctors
        database.child("doctors").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding != null) {
                    binding.tvTotalDoctorsCount.text = snapshot.childrenCount.toString()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        // Fetch Total Users (Patients)
        database.child("users").orderByChild("role").equalTo("Patient").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding != null) {
                    binding.tvTotalUsersCount.text = snapshot.childrenCount.toString()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
        
        // Avg Rating is usually calculated or stored as a separate metric
        binding.tvAvgRatingCount.text = "4.8" 
    }

    private fun fetchTopDoctors() {
        database.child("doctors").limitToFirst(5).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                topDoctorsList.clear()
                for (postSnapshot in snapshot.children) {
                    val doctor = postSnapshot.getValue(DoctorModel::class.java)
                    doctor?.let { topDoctorsList.add(it) }
                }
                
                if (topDoctorsList.isEmpty()) {
                    // Fallback to static data
                    topDoctorsList.addAll(listOf(
                        DoctorModel("Dr. Sarah Johnson", "Cardiologist", "Apollo Hospital", rating = 4.9, imageRes = R.drawable.ic_doctor1),
                        DoctorModel("Dr. Rahul Mehta", "Neurologist", "Fortis Hospital", rating = 4.8, imageRes = R.drawable.ic_doctor2),
                        DoctorModel("Dr. Priya Nair", "Dermatologist", "AIIMS Delhi", rating = 4.7, imageRes = R.drawable.ic_doctor3),
                        DoctorModel("Dr. Amit Verma", "Pediatrician", "Max Healthcare", rating = 4.9, imageRes = R.drawable.ic_doctor4)
                    ))
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load top doctors", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
