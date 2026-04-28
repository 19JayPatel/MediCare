package com.example.medicare.Doctor.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicare.Doctor.adapter.DoctorAppointmentsAdapter
import com.example.medicare.Patient.models.AppointmentModel
import com.example.medicare.databinding.FragmentDoctorAppointmentsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DoctorAppointmentsFragment : Fragment() {

    private var _binding: FragmentDoctorAppointmentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private val appointmentList = mutableListOf<AppointmentModel>()
    private lateinit var adapter: DoctorAppointmentsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorAppointmentsBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        fetchDoctorAppointments()
    }

    private fun setupRecyclerView() {
        adapter = DoctorAppointmentsAdapter(appointmentList) { appointment, newStatus ->
            updateAppointmentStatus(appointment, newStatus)
        }
        binding.rvAppointments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@DoctorAppointmentsFragment.adapter
        }
    }

    private fun fetchDoctorAppointments() {
        val doctorUid = auth.currentUser?.uid ?: return
        database.child("Appointments").orderByChild("doctorUid").equalTo(doctorUid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    appointmentList.clear()
                    for (postSnapshot in snapshot.children) {
                        val appointment = postSnapshot.getValue(AppointmentModel::class.java)
                        if (appointment != null) {
                            appointmentList.add(appointment)
                        }
                    }
                    
                    if (appointmentList.isEmpty()) {
                        binding.rvAppointments.visibility = View.GONE
                        // Show empty state if you have one
                    } else {
                        binding.rvAppointments.visibility = View.VISIBLE
                        // Sort by creation time (newest first)
                        appointmentList.sortByDescending { it.createdAt }
                        adapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to load appointments", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun updateAppointmentStatus(appointment: AppointmentModel, newStatus: String) {
        if (appointment.bookingId.isNotEmpty()) {
            database.child("Appointments").child(appointment.bookingId).child("status").setValue(newStatus)
                .addOnSuccessListener {
                    Toast.makeText(context, "Status updated to $newStatus", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
