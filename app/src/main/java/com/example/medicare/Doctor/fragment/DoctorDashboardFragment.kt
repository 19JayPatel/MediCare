package com.example.medicare.Doctor.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.medicare.Doctor.adapter.DoctorAppointmentsAdapter
import com.example.medicare.Patient.models.AppointmentModel
import com.example.medicare.R
import com.example.medicare.databinding.FragmentDoctorDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class DoctorDashboardFragment : Fragment() {

    private var _binding: FragmentDoctorDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private val todayAppointments = mutableListOf<AppointmentModel>()
    private lateinit var adapter: DoctorAppointmentsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorDashboardBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        fetchDoctorInfo()
        fetchDashboardStats()
    }

    private fun setupRecyclerView() {
        // Reusing DoctorAppointmentsAdapter for consistency
        adapter = DoctorAppointmentsAdapter(todayAppointments) { appointment, newStatus ->
            updateAppointmentStatus(appointment, newStatus)
        }
        binding.rvTodayAppointments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@DoctorDashboardFragment.adapter
            isNestedScrollingEnabled = false
        }
    }

    private fun fetchDoctorInfo() {
        val userId = auth.currentUser?.uid ?: return
        database.child("Doctors").child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding == null) return
                
                val name = snapshot.child("fullName").value as? String ?: "Doctor"
                binding.tvHeaderSubtitle.text = "Good morning, Dr. $name 👋"
                
                val imageUrl = snapshot.child("imageUrl").value as? String
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(requireContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_person)
                        .into(binding.ivDoctorProfile)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun fetchDashboardStats() {
        val doctorUid = auth.currentUser?.uid ?: return
        val todayStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())

        database.child("Appointments").orderByChild("doctorUid").equalTo(doctorUid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (_binding == null) return

                    var total = 0
                    var today = 0
                    var pending = 0
                    var completed = 0
                    todayAppointments.clear()

                    for (snap in snapshot.children) {
                        val appt = snap.getValue(AppointmentModel::class.java)
                        appt?.let {
                            total++
                            
                            // 1. Check if appointment is today
                            if (it.appointmentDate == todayStr) {
                                today++
                                // Add today's "Upcoming" appointments to list
                                if (it.status == "Upcoming") {
                                    todayAppointments.add(it)
                                }
                            }

                            // 2. Count statuses
                            when (it.status) {
                                "Upcoming" -> pending++ // Or however you define "Requests/Pending"
                                "Completed" -> completed++
                            }
                        }
                    }

                    // Update UI Counts
                    binding.tvTotalAppointmentsCount.text = total.toString()
                    binding.tvTodayAppointmentsCount.text = today.toString()
                    binding.tvPendingRequestsCount.text = pending.toString()
                    binding.tvCompletedCount.text = completed.toString()

                    // Update List
                    todayAppointments.sortByDescending { it.createdAt }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Dashboard", "Error: ${error.message}")
                }
            })
    }

    private fun updateAppointmentStatus(appointment: AppointmentModel, newStatus: String) {
        if (appointment.bookingId.isNotEmpty()) {
            database.child("Appointments").child(appointment.bookingId).child("status").setValue(newStatus)
                .addOnSuccessListener {
                    Toast.makeText(context, "Status updated", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
