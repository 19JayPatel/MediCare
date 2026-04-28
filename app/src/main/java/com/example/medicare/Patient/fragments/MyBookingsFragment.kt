package com.example.medicare.Patient.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicare.Patient.adapters.BookingAdapter
import com.example.medicare.Patient.adapters.BookingModel
import com.example.medicare.Patient.activities.MainActivity
import com.example.medicare.R
import com.example.medicare.databinding.FragmentMyBookingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyBookingsFragment : Fragment() {

    private var _binding: FragmentMyBookingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private val bookingList = mutableListOf<BookingModel>()
    private lateinit var adapter: BookingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyBookingsBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            (activity as? MainActivity)?.navigateToHome()
        }

        setupBookingList()
        fetchMyBookings()
    }

    private fun setupBookingList() {
        adapter = BookingAdapter(bookingList, { booking ->
            cancelBooking(booking)
        }, { booking ->
            Toast.makeText(requireContext(), "Rescheduled: ${booking.doctorName}", Toast.LENGTH_SHORT).show()
        })
        binding.bookingRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.bookingRecycler.adapter = adapter
    }

    private fun fetchMyBookings() {
        val userId = auth.currentUser?.uid ?: return
        database.child("appointments").orderByChild("patientId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    bookingList.clear()
                    for (postSnapshot in snapshot.children) {
                        val booking = postSnapshot.getValue(BookingModel::class.java)
                        booking?.let { bookingList.add(it) }
                    }
                    
                    if (bookingList.isEmpty()) {
                        // Fallback or empty state
                        binding.bookingRecycler.visibility = View.GONE
                        // You might want to show an empty state view here
                    } else {
                        binding.bookingRecycler.visibility = View.VISIBLE
                        adapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to load bookings", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun cancelBooking(booking: BookingModel) {
        if (booking.bookingId.isNotEmpty()) {
            database.child("appointments").child(booking.bookingId).removeValue()
                .addOnSuccessListener {
                    Toast.makeText(context, "Booking canceled", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
