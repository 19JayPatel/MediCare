package com.example.medicare.Patient.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicare.Doctor.model.ScheduleModel
import com.example.medicare.Patient.adapters.TimeSlotAdapter
import com.example.medicare.Patient.model.SlotType
import com.example.medicare.Patient.model.TimeSlotModel
import com.example.medicare.Patient.models.DoctorModel
import com.example.medicare.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class SelectTimeSlotActivity : AppCompatActivity() {

    private lateinit var morningAdapter: TimeSlotAdapter
    private lateinit var afternoonAdapter: TimeSlotAdapter
    private var selectedDate: String = ""
    private var selectedTime: String? = null

    // For rotation handling
    private var selectedYear: Int = -1
    private var selectedMonth: Int = -1
    private var selectedDay: Int = -1

    private var doctorUid: String? = null
    private var doctorModel: DoctorModel? = null
    private var doctorSchedules = mutableListOf<ScheduleModel>()
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_time_slot)

        doctorUid = intent.getStringExtra("doctorUid")
        database = FirebaseDatabase.getInstance().reference

        if (doctorUid == null) {
            Toast.makeText(this, "Doctor ID not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Restore state if rotating
        if (savedInstanceState != null) {
            selectedDate = savedInstanceState.getString("selectedDate", "")
            selectedYear = savedInstanceState.getInt("selectedYear", -1)
            selectedMonth = savedInstanceState.getInt("selectedMonth", -1)
            selectedDay = savedInstanceState.getInt("selectedDay", -1)
            selectedTime = savedInstanceState.getString("selectedTime")
        } else {
            // Initialize with today's date format for the first time
            val calendar = Calendar.getInstance()
            selectedYear = calendar.get(Calendar.YEAR)
            selectedMonth = calendar.get(Calendar.MONTH)
            selectedDay = calendar.get(Calendar.DAY_OF_MONTH)
            selectedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(calendar.time)
        }

        setupAdapters()
        fetchDoctorData()
        fetchDoctorSchedules()

        findViewById<MaterialButton>(R.id.btnConfirmBooking).setOnClickListener {
            // Final validation before booking
            if (!isValidBooking()) {
                return@setOnClickListener
            }

            if (selectedTime == null) {
                Toast.makeText(this, "Please select a time slot", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, BookingConfirmationActivity::class.java).apply {
                    putExtra("doctorUid", doctorUid)
                    putExtra("doctorName", doctorModel?.fullName ?: "")
                    putExtra("specialty", doctorModel?.specialty ?: "")
                    putExtra("clinicName", doctorModel?.clinicName ?: "")
                    putExtra("selectedDate", selectedDate)
                    putExtra("selectedTime", selectedTime)
                }
                startActivity(intent)
            }
        }

        findViewById<View>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    // Save state on rotation
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("selectedDate", selectedDate)
        outState.putInt("selectedYear", selectedYear)
        outState.putInt("selectedMonth", selectedMonth)
        outState.putInt("selectedDay", selectedDay)
        outState.putString("selectedTime", selectedTime)
    }

    private fun fetchDoctorData() {
        val uid = doctorUid ?: return
        database.child("Doctors").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                doctorModel = snapshot.getValue(DoctorModel::class.java)
                doctorModel?.let {
                    findViewById<TextView>(R.id.tvDoctorName).text = "Dr. ${it.fullName}"
                    findViewById<TextView>(R.id.tvDoctorDetails).text = "${it.specialty} • ${it.clinicName}"
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun fetchDoctorSchedules() {
        val uid = doctorUid ?: return
        database.child("DoctorSchedules").child(uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                doctorSchedules.clear()
                for (postSnapshot in snapshot.children) {
                    val schedule = postSnapshot.getValue(ScheduleModel::class.java)
                    schedule?.let { doctorSchedules.add(it) }
                }
                setupCalendar()
                
                // Reload slots for the currently selected date
                val cal = Calendar.getInstance()
                cal.set(selectedYear, selectedMonth, selectedDay)
                checkAvailabilityAndGenerateSlots(cal)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setupAdapters() {
        morningAdapter = TimeSlotAdapter(mutableListOf()) { slot ->
            selectedTime = slot.time
            afternoonAdapter.clearSelection()
        }

        afternoonAdapter = TimeSlotAdapter(mutableListOf()) { slot ->
            selectedTime = slot.time
            morningAdapter.clearSelection()
        }

        findViewById<RecyclerView>(R.id.rvMorningSlots).apply {
            layoutManager = GridLayoutManager(this@SelectTimeSlotActivity, 3)
            adapter = morningAdapter
        }

        findViewById<RecyclerView>(R.id.rvAfternoonSlots).apply {
            layoutManager = GridLayoutManager(this@SelectTimeSlotActivity, 3)
            adapter = afternoonAdapter
        }
    }

    private fun setupCalendar() {
        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        
        // PROBLEM 2 FIX: Prevent past dates selection
        // Set minimum date to today
        val minCalendar = Calendar.getInstance()
        minCalendar.set(Calendar.HOUR_OF_DAY, 0)
        minCalendar.set(Calendar.MINUTE, 0)
        minCalendar.set(Calendar.SECOND, 0)
        minCalendar.set(Calendar.MILLISECOND, 0)
        calendarView.minDate = minCalendar.timeInMillis

        // PROBLEM 1 FIX: Keep selected date after rotation
        if (selectedYear != -1) {
            val restoreCal = Calendar.getInstance()
            restoreCal.set(selectedYear, selectedMonth, selectedDay)
            calendarView.date = restoreCal.timeInMillis
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Store selected date parts for rotation
            selectedYear = year
            selectedMonth = month
            selectedDay = dayOfMonth
            
            val cal = Calendar.getInstance()
            cal.set(year, month, dayOfMonth)
            checkAvailabilityAndGenerateSlots(cal)
        }
    }

    private fun checkAvailabilityAndGenerateSlots(cal: Calendar) {
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val selectedDayName = dayFormat.format(cal.time)

        // Update selectedDate string
        selectedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(cal.time)

        // Find schedule for the selected day
        val schedule = doctorSchedules.find { it.day.equals(selectedDayName, ignoreCase = true) }

        if (schedule != null) {
            generateSlotsFromSchedule(schedule)
        } else {
            morningAdapter.updateData(emptyList())
            afternoonAdapter.updateData(emptyList())
            Toast.makeText(
                this,
                "Doctor is not available on this day.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun generateSlotsFromSchedule(schedule: ScheduleModel) {
        val startTimeStr = schedule.startTime
        val endTimeStr = schedule.endTime
        val duration = schedule.slotDuration.filter { it.isDigit() }.toIntOrNull() ?: 30

        if (startTimeStr.isEmpty() || endTimeStr.isEmpty()) return

        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val startTime = Calendar.getInstance()
        try {
            startTime.time = sdf.parse(startTimeStr) ?: return
        } catch (e: Exception) { return }

        val endTime = Calendar.getInstance()
        try {
            endTime.time = sdf.parse(endTimeStr) ?: return
        } catch (e: Exception) { return }

        // Fetch booked slots
        database.child("Appointments").orderByChild("doctorUid").equalTo(doctorUid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val bookedSlots = mutableSetOf<String>()
                    for (postSnapshot in snapshot.children) {
                        val appointment = postSnapshot.getValue(com.example.medicare.Patient.models.AppointmentModel::class.java)
                        if (appointment != null && appointment.appointmentDate == selectedDate && appointment.status != "Cancelled") {
                            bookedSlots.add(appointment.appointmentTime)
                        }
                    }

                    val morningSlots = mutableListOf<TimeSlotModel>()
                    val afternoonSlots = mutableListOf<TimeSlotModel>()

                    val currentSlot = startTime.clone() as Calendar
                    
                    // REAL-TIME SLOT FILTERING: Get current time if selected date is today
                    val isToday = isSelectedDateToday()
                    val now = Calendar.getInstance()

                    while (currentSlot.before(endTime)) {
                        val slotTimeStr = sdf.format(currentSlot.time)
                        
                        // Check if slot is in the past (if selected date is today)
                        var isPastSlot = false
                        if (isToday) {
                            val slotCal = Calendar.getInstance()
                            slotCal.set(Calendar.HOUR_OF_DAY, currentSlot.get(Calendar.HOUR_OF_DAY))
                            slotCal.set(Calendar.MINUTE, currentSlot.get(Calendar.MINUTE))
                            slotCal.set(Calendar.SECOND, 0)
                            
                            if (slotCal.before(now)) {
                                isPastSlot = true
                            }
                        }

                        // Only show future slots for today
                        if (!isPastSlot) {
                            val isAvailable = !bookedSlots.contains(slotTimeStr)
                            val hour = currentSlot.get(Calendar.HOUR_OF_DAY)
                            
                            if (hour < 12) {
                                morningSlots.add(TimeSlotModel(slotTimeStr, isAvailable = isAvailable, type = SlotType.MORNING))
                            } else {
                                afternoonSlots.add(TimeSlotModel(slotTimeStr, isAvailable = isAvailable, type = SlotType.AFTERNOON))
                            }
                        }

                        currentSlot.add(Calendar.MINUTE, duration)
                    }

                    morningAdapter.updateData(morningSlots)
                    afternoonAdapter.updateData(afternoonSlots)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    // Helper to check if selected date is today
    private fun isSelectedDateToday(): Boolean {
        val today = Calendar.getInstance()
        return selectedYear == today.get(Calendar.YEAR) &&
               selectedMonth == today.get(Calendar.MONTH) &&
               selectedDay == today.get(Calendar.DAY_OF_MONTH)
    }

    // Final booking validation
    private fun isValidBooking(): Boolean {
        val today = Calendar.getInstance()
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)

        val selected = Calendar.getInstance()
        selected.set(selectedYear, selectedMonth, selectedDay, 0, 0, 0)
        selected.set(Calendar.MILLISECOND, 0)

        if (selected.before(today)) {
            Toast.makeText(this, "Cannot book appointments for past dates", Toast.LENGTH_SHORT).show()
            return false
        }
        
        // If today, check time
        if (isSelectedDateToday() && selectedTime != null) {
            val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val slotTime = Calendar.getInstance()
            try {
                slotTime.time = sdf.parse(selectedTime!!) ?: return false
                
                val now = Calendar.getInstance()
                val slotFullTime = Calendar.getInstance()
                slotFullTime.set(Calendar.HOUR_OF_DAY, slotTime.get(Calendar.HOUR_OF_DAY))
                slotFullTime.set(Calendar.MINUTE, slotTime.get(Calendar.MINUTE))
                
                if (slotFullTime.before(now)) {
                    Toast.makeText(this, "This time slot has already passed", Toast.LENGTH_SHORT).show()
                    return false
                }
            } catch (e: Exception) {
                return false
            }
        }
        
        return true
    }
}
