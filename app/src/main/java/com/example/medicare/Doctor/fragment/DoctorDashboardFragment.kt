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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class DoctorDashboardFragment : Fragment() {

    private var _binding: FragmentDoctorDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private val appointmentList = mutableListOf<AppointmentModel>()
    private lateinit var adapter: DashboardAppointmentsAdapter

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
        setupClickListeners()
        fetchDoctorData()
        fetchAppointments()
    }

    private fun setupRecyclerView() {
        adapter = DashboardAppointmentsAdapter(appointmentList)
        binding.rvTodayAppointments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@DoctorDashboardFragment.adapter
            isNestedScrollingEnabled = false
        }
    }

    private fun fetchDoctorData() {
        val userId = auth.currentUser?.uid ?: return
        database.child("users").child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding != null) {
                    val name = snapshot.child("fullName").value as? String ?: "Doctor"
                    binding.tvHeaderSubtitle.text = "Good morning, Dr. $name 👋"
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun fetchAppointments() {
        val doctorId = auth.currentUser?.uid ?: return
        val todayDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())

        database.child("appointments").orderByChild("doctorId").equalTo(doctorId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (_binding == null) return
                    
                    val allAppts = mutableListOf<AppointmentModel>()
                    var total = 0
                    var todayCount = 0
                    var pendingCount = 0
                    var completedCount = 0

                    for (postSnapshot in snapshot.children) {
                        val appt = postSnapshot.getValue(AppointmentModel::class.java)
                        appt?.let {
                            total++
                            if (it.date == todayDate) todayCount++
                            if (it.status == "Pending") pendingCount++
                            if (it.status == "Confirmed" || it.status == "Accepted") completedCount++ // Assuming Accepted/Confirmed is completed or upcoming
                            
                            // For the dashboard list, show today's accepted/confirmed appointments
                            if (it.date == todayDate && (it.status == "Accepted" || it.status == "Confirmed")) {
                                allAppts.add(it)
                            }
                        }
                    }

                    binding.tvTotalAppointmentsCount.text = total.toString()
                    binding.tvTodayAppointmentsCount.text = todayCount.toString()
                    binding.tvPendingRequestsCount.text = pendingCount.toString()
                    binding.tvCompletedCount.text = completedCount.toString()

                    appointmentList.clear()
                    appointmentList.addAll(allAppts)
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to load appointments", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun setupClickListeners() {
        binding.tvSeeAll.setOnClickListener {
            // Navigate to Appointments Fragment if possible via activity
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
