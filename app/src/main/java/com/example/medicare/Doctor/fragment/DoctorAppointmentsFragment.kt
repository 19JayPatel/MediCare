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
        setupClickListeners()
        fetchAppointments()
    }

    private fun setupRecyclerView() {
        adapter = DoctorAppointmentsAdapter(appointmentList)
        binding.rvAppointments.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@DoctorAppointmentsFragment.adapter
        }
    }

    private fun fetchAppointments() {
        val doctorId = auth.currentUser?.uid ?: return
        database.child("appointments").orderByChild("doctorId").equalTo(doctorId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    appointmentList.clear()
                    for (postSnapshot in snapshot.children) {
                        val appointment = postSnapshot.getValue(AppointmentModel::class.java)
                        appointment?.let { 
                            // Ensure the ID is set from the key if not already in the model
                            val apptWithId = it.copy(id = postSnapshot.key ?: it.id)
                            appointmentList.add(apptWithId) 
                        }
                    }
                    
                    if (appointmentList.isEmpty()) {
                        // Handle empty state if needed
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to load appointments", Toast.LENGTH_SHORT).show()
                }
            })
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
