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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class DoctorScheduleFragment : Fragment() {

    private var _binding: FragmentDoctorScheduleBinding? = null
    private val binding get() = _binding!!

    private lateinit var scheduleAdapter: ScheduleAdapter
    private val scheduleList = mutableListOf<ScheduleModel>()

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var editingScheduleId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorScheduleBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        fetchSchedules()
    }

    private fun setupRecyclerView() {
        scheduleAdapter = ScheduleAdapter(
            scheduleList,
            onEditClick = { schedule ->
                editSchedule(schedule)
            },
            onDeleteClick = { schedule ->
                deleteSchedule(schedule)
            }
        )
        binding.rvSlots.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = scheduleAdapter
        }
    }

    private fun fetchSchedules() {
        val doctorUid = auth.currentUser?.uid ?: return
        database.child("DoctorSchedules").child(doctorUid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    scheduleList.clear()
                    for (postSnapshot in snapshot.children) {
                        val schedule = postSnapshot.getValue(ScheduleModel::class.java)
                        schedule?.let { scheduleList.add(it) }
                    }
                    scheduleAdapter.updateData(scheduleList)
                    updateAvailableSlotsCount()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
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
            saveSchedule()
        }

        binding.btnAddHeader.setOnClickListener {
            resetForm()
        }
    }

    private fun saveSchedule() {
        val selectedChipId = binding.cgDays.checkedChipId
        if (selectedChipId == -1) {
            Toast.makeText(requireContext(), "Please select a day", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedDay = when (selectedChipId) {
            binding.chipMon.id -> "Monday"
            binding.chipTue.id -> "Tuesday"
            binding.chipWed.id -> "Wednesday"
            binding.chipThu.id -> "Thursday"
            binding.chipFri.id -> "Friday"
            binding.chipSat.id -> "Saturday"
            binding.chipSun.id -> "Sunday"
            else -> ""
        }

        val startTime = binding.tvStartTime.text.toString()
        val endTime = binding.tvEndTime.text.toString()
        val duration = binding.tvDuration.text.toString().replace(" per slot", "")

        val doctorUid = auth.currentUser?.uid ?: return

        // Check for duplicate day if not editing
        if (editingScheduleId == null) {
            val exists = scheduleList.any { it.day.equals(selectedDay, ignoreCase = true) }
            if (exists) {
                Toast.makeText(requireContext(), "Schedule for $selectedDay already exists.", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val scheduleId = editingScheduleId ?: database.child("DoctorSchedules").child(doctorUid).push().key ?: UUID.randomUUID().toString()
        val schedule = ScheduleModel(scheduleId, selectedDay, startTime, endTime, duration)

        database.child("DoctorSchedules").child(doctorUid).child(scheduleId).setValue(schedule)
            .addOnSuccessListener {
                val msg = if (editingScheduleId != null) "Schedule updated" else "Schedule added"
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                resetForm()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to save schedule", Toast.LENGTH_SHORT).show()
            }
    }

    private fun editSchedule(schedule: ScheduleModel) {
        editingScheduleId = schedule.id
        binding.btnAddSlot.text = "Update Slot"

        // Set Day Chip
        val chipId = when (schedule.day) {
            "Monday" -> binding.chipMon.id
            "Tuesday" -> binding.chipTue.id
            "Wednesday" -> binding.chipWed.id
            "Thursday" -> binding.chipThu.id
            "Friday" -> binding.chipFri.id
            "Saturday" -> binding.chipSat.id
            "Sunday" -> binding.chipSun.id
            else -> -1
        }
        if (chipId != -1) {
            binding.cgDays.check(chipId)
        }

        binding.tvStartTime.text = schedule.startTime
        binding.tvEndTime.text = schedule.endTime
        binding.tvDuration.text = "${schedule.slotDuration} per slot"
    }

    private fun deleteSchedule(schedule: ScheduleModel) {
        val doctorUid = auth.currentUser?.uid ?: return
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Schedule")
            .setMessage("Are you sure you want to delete the schedule for ${schedule.day}?")
            .setPositiveButton("Delete") { _, _ ->
                database.child("DoctorSchedules").child(doctorUid).child(schedule.id).removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Schedule deleted", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun resetForm() {
        editingScheduleId = null
        binding.btnAddSlot.text = "Add Slot"
        binding.cgDays.clearCheck()
        binding.tvStartTime.text = "09:00 AM"
        binding.tvEndTime.text = "05:00 PM"
        binding.tvDuration.text = "30 Minutes per slot"
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
