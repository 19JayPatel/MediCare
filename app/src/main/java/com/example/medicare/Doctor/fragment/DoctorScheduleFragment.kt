package com.example.medicare.Doctor.fragment

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicare.Doctor.adapter.ScheduleAdapter
import com.example.medicare.Doctor.model.ScheduleModel
import com.example.medicare.databinding.FragmentDoctorScheduleBinding
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.*

class DoctorScheduleFragment : Fragment() {

    private var _binding: FragmentDoctorScheduleBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var scheduleAdapter: ScheduleAdapter
    private val scheduleList = mutableListOf<ScheduleModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        // Dummy Data
        if (scheduleList.isEmpty()) {
            scheduleList.addAll(listOf(
                ScheduleModel("1", "Monday", "09:00 AM", "05:00 PM", "30 min"),
                ScheduleModel("2", "Tuesday", "10:00 AM", "04:00 PM", "30 min"),
                ScheduleModel("3", "Wednesday", "09:00 AM", "05:00 PM", "30 min"),
                ScheduleModel("4", "Thursday", "09:00 AM", "02:00 PM", "30 min"),
                ScheduleModel("5", "Friday", "08:00 AM", "12:00 PM", "30 min")
            ))
        }

        scheduleAdapter = ScheduleAdapter(scheduleList)
        binding.rvSlots.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = scheduleAdapter
        }
        
        updateAvailableSlotsCount()
    }

    private fun setupClickListeners() {
        binding.llStartTime.setOnClickListener {
            showTimePicker { time ->
                binding.tvStartTime.text = time
            }
        }

        binding.llEndTime.setOnClickListener {
            showTimePicker { time ->
                binding.tvEndTime.text = time
            }
        }

        binding.llDuration.setOnClickListener {
            showDurationPicker()
        }

        binding.btnAddSlot.setOnClickListener {
            val selectedChipId = binding.cgDays.checkedChipId
            if (selectedChipId != -1) {
                val selectedDay = binding.cgDays.findViewById<Chip>(selectedChipId).text.toString()
                val startTime = binding.tvStartTime.text.toString()
                val endTime = binding.tvEndTime.text.toString()
                val duration = binding.tvDuration.text.toString().replace(" per slot", "")

                val newSlot = ScheduleModel(
                    UUID.randomUUID().toString(),
                    selectedDay,
                    startTime,
                    endTime,
                    duration
                )

                scheduleList.add(newSlot)
                scheduleAdapter.notifyItemInserted(scheduleList.size - 1)
                updateAvailableSlotsCount()
                Toast.makeText(requireContext(), "Slot added for $selectedDay", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Please select a day", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.btnAddHeader.setOnClickListener {
             Toast.makeText(requireContext(), "Add new slot action", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showTimePicker(onTimeSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                val selectedTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, selectedHour)
                    set(Calendar.MINUTE, selectedMinute)
                }
                val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
                onTimeSelected(format.format(selectedTime.time))
            },
            hour,
            minute,
            false
        )
        timePickerDialog.show()
    }

    private fun showDurationPicker() {
        val durations = arrayOf("15 Minutes", "30 Minutes", "45 Minutes", "60 Minutes")
        AlertDialog.Builder(requireContext())
            .setTitle("Select Slot Duration")
            .setItems(durations) { _, which ->
                binding.tvDuration.text = "${durations[which]} per slot"
            }
            .show()
    }

    private fun updateAvailableSlotsCount() {
        binding.tvAvailableSlotsLabel.text = "AVAILABLE SLOTS (${scheduleList.size})"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}