package com.example.medicare.Patient.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicare.Patient.adapters.BookingAdapter
import com.example.medicare.Patient.models.AppointmentModel
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
    private val bookingList = mutableListOf<AppointmentModel>()
    private lateinit var adapter: BookingAdapter
    private var currentStatusFilter = "Upcoming"

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

        setupTabs()
        setupBookingList()
        fetchMyBookings() // Initial fetch
    }

    private fun setupTabs() {
        binding.tabUpcoming.setOnClickListener {
            updateTabUI("Upcoming")
            fetchMyBookings()
        }
        binding.tabCompleted.setOnClickListener {
            updateTabUI("Completed")
            fetchMyBookings()
        }
        binding.tabCanceled.setOnClickListener {
            updateTabUI("Cancelled")
            fetchMyBookings()
        }
        
        // Default UI state
        updateTabUI("Upcoming")
    }

    private fun updateTabUI(status: String) {
        currentStatusFilter = status
        
        // Reset all tabs
        resetTabStyle(binding.tabUpcoming)
        resetTabStyle(binding.tabCompleted)
        resetTabStyle(binding.tabCanceled)

        // Set active tab
        when (status) {
            "Upcoming" -> setActiveTabStyle(binding.tabUpcoming)
            "Completed" -> setActiveTabStyle(binding.tabCompleted)
            "Cancelled" -> setActiveTabStyle(binding.tabCanceled)
        }
    }

    private fun resetTabStyle(tab: View) {
        val viewGroup = tab as ViewGroup
        val textView = viewGroup.getChildAt(0) as android.widget.TextView
        val indicator = viewGroup.getChildAt(1)
        
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
        textView.setTypeface(null, android.graphics.Typeface.NORMAL)
        
        indicator.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.border))
        val params = indicator.layoutParams
        params.height = (1 * resources.displayMetrics.density).toInt() // 1dp
        indicator.layoutParams = params
    }

    private fun setActiveTabStyle(tab: View) {
        val viewGroup = tab as ViewGroup
        val textView = viewGroup.getChildAt(0) as android.widget.TextView
        val indicator = viewGroup.getChildAt(1)
        
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
        textView.setTypeface(null, android.graphics.Typeface.BOLD)
        
        indicator.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primary))
        val params = indicator.layoutParams
        params.height = (3 * resources.displayMetrics.density).toInt() // 3dp
        indicator.layoutParams = params
    }

    private fun setupBookingList() {
        adapter = BookingAdapter(bookingList) { booking ->
            showCancelDialog(booking)
        }
        binding.bookingRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.bookingRecycler.adapter = adapter
    }

    private fun fetchMyBookings() {
        val userId = auth.currentUser?.uid ?: return
        // Use ValueEventListener for real-time updates
        database.child("Appointments").orderByChild("patientUid").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!isAdded) return
                    
                    bookingList.clear()
                    for (postSnapshot in snapshot.children) {
                        val booking = postSnapshot.getValue(AppointmentModel::class.java)
                        if (booking != null && booking.status == currentStatusFilter) {
                            bookingList.add(booking)
                        }
                    }
                    
                    if (bookingList.isEmpty()) {
                        binding.bookingRecycler.visibility = View.GONE
                    } else {
                        binding.bookingRecycler.visibility = View.VISIBLE
                        // Sort by date (newest first based on createdAt)
                        bookingList.sortByDescending { it.createdAt }
                        adapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    if (isAdded) {
                        Toast.makeText(context, "Failed to load bookings: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    private fun showCancelDialog(booking: AppointmentModel) {
        AlertDialog.Builder(requireContext())
            .setTitle("Cancel Appointment")
            .setMessage("Are you sure you want to cancel this appointment?")
            .setPositiveButton("Yes") { _, _ ->
                cancelBooking(booking)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun cancelBooking(booking: AppointmentModel) {
        if (booking.bookingId.isNotEmpty()) {
            database.child("Appointments").child(booking.bookingId).child("status").setValue("Cancelled")
                .addOnSuccessListener {
                    if (isAdded) {
                        Toast.makeText(context, "Appointment Cancelled Successfully", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    if (isAdded) {
                        Toast.makeText(context, "Failed to cancel appointment", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
