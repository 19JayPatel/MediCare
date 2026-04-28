package com.example.medicare.Admin.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.medicare.Admin.adapter.TopDoctorsAdapter
import com.example.medicare.Admin.model.DoctorModel
import com.example.medicare.R
import com.example.medicare.databinding.FragmentAdminDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

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
        fetchAdminInfo()
        fetchStats()
        fetchTopDoctors()
    }

    private fun setupTopDoctorsRecyclerView() {
        adapter = TopDoctorsAdapter(topDoctorsList)
        binding.rvTopDoctors.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTopDoctors.adapter = adapter
    }

    private fun fetchAdminInfo() {
        val uid = auth.currentUser?.uid ?: return
        database.child("users").child(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding == null) return
                val name = snapshot.child("fullName").value as? String ?: "Admin"
                binding.tvSubtitle.text = "Good morning, $name 👋"
                
                val imageUrl = snapshot.child("imageUrl").value as? String
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(requireContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_person)
                        .into(binding.ivAdminProfile)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun fetchStats() {
        // Fetch Total Bookings (Appointments)
        database.child("Appointments").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding != null) {
                    binding.tvTotalBookingsCount.text = snapshot.childrenCount.toString()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        // Fetch Total Doctors and Average Rating
        database.child("Doctors").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding == null) return
                binding.tvTotalDoctorsCount.text = snapshot.childrenCount.toString()
                
                var totalRating = 0.0
                var count = 0
                for (docSnap in snapshot.children) {
                    val ratingValue = docSnap.child("rating").value
                    val rating = when (ratingValue) {
                        is Double -> ratingValue
                        is Long -> ratingValue.toDouble()
                        is String -> ratingValue.toDoubleOrNull() ?: 0.0
                        else -> 0.0
                    }
                    if (rating > 0) {
                        totalRating += rating
                        count++
                    }
                }
                val avg = if (count > 0) totalRating / count else 0.0
                binding.tvAvgRatingCount.text = String.format(Locale.US, "%.1f", avg)
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        // Fetch Total Users (Patients only)
        database.child("users").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding == null) return
                var patientCount = 0
                for (userSnap in snapshot.children) {
                    val role = userSnap.child("role").value as? String
                    if (role == "Patient") {
                        patientCount++
                    }
                }
                binding.tvTotalUsersCount.text = patientCount.toString()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun fetchTopDoctors() {
        // Fetch all doctors and sort them by rating manually for better control
        database.child("Doctors").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding == null) return
                val tempList = mutableListOf<DoctorModel>()
                for (postSnapshot in snapshot.children) {
                    val name = postSnapshot.child("fullName").value as? String ?: ""
                    val specialty = postSnapshot.child("specialty").value as? String ?: ""
                    val hospital = postSnapshot.child("hospital").value as? String ?: ""
                    val ratingValue = postSnapshot.child("rating").value
                    val rating = when (ratingValue) {
                        is Double -> ratingValue
                        is Long -> ratingValue.toDouble()
                        is String -> ratingValue.toDoubleOrNull() ?: 0.0
                        else -> 0.0
                    }
                    val imageUrl = postSnapshot.child("imageUrl").value as? String ?: ""
                    
                    tempList.add(DoctorModel(
                        name = "Dr. $name",
                        specialization = specialty,
                        hospital = hospital,
                        rating = rating,
                        imageUrl = imageUrl
                    ))
                }
                
                // Sort by rating descending and take top 5
                topDoctorsList.clear()
                topDoctorsList.addAll(tempList.sortedByDescending { it.rating }.take(5))
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("AdminDashboard", "Error: ${error.message}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
