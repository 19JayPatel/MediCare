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

class MyBookingsFragment : Fragment() {

    private var _binding: FragmentMyBookingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyBookingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            (activity as? MainActivity)?.navigateToHome()
        }

        setupBookingList()
    }

    private fun setupBookingList() {
        val bookings = arrayListOf(
            BookingModel(
                "May 22, 2023 - 10.00 AM",
                "Dr. Jatin Patel",
                "Orthopedic Surgery",
                "Elite Ortho Clinic, USA",
                R.drawable.ic_doctor1
            ),
            BookingModel(
                "June 14, 2023 - 15.00 PM",
                "Dr. Danesh Shah",
                "Gastroenterologist",
                "Digestive Institute, USA",
                R.drawable.ic_doctor2
            ),
            BookingModel(
                "June 21, 2023 - 10.00 AM",
                "Dr. Navan Yadav",
                "Pediatrics",
                "Serenity Pediatrics Clinic",
                R.drawable.ic_doctor3
            )
        )

        binding.bookingRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.bookingRecycler.adapter = BookingAdapter(bookings, { booking ->
            Toast.makeText(requireContext(), "Canceled: ${booking.doctorName}", Toast.LENGTH_SHORT)
                .show()
        }, { booking ->
            Toast.makeText(
                requireContext(),
                "Rescheduled: ${booking.doctorName}",
                Toast.LENGTH_SHORT
            ).show()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}