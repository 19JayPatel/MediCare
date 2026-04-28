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

        // Initialize with today's date format
        val calendar = Calendar.getInstance()
        selectedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(calendar.time)

        setupAdapters()
        fetchDoctorData()
        fetchDoctorSchedules()

        findViewById<MaterialButton>(R.id.btnConfirmBooking).setOnClickListener {
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
                // Initial load for today if available
                checkAvailabilityAndGenerateSlots(Calendar.getInstance())
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
        val currentTime = System.currentTimeMillis()
        calendarView.minDate = currentTime - 1000

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val cal = Calendar.getInstance()
            cal.set(year, month, dayOfMonth)
            checkAvailabilityAndGenerateSlots(cal)
        }
    }

    private fun checkAvailabilityAndGenerateSlots(cal: Calendar) {
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        val selectedDayName = dayFormat.format(cal.time)

        // Find schedule for the selected day
        val schedule = doctorSchedules.find { it.day.equals(selectedDayName, ignoreCase = true) }

        if (schedule != null) {
            selectedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(cal.time)
            selectedTime = null
            generateSlotsFromSchedule(schedule)
        } else {
            selectedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(cal.time)
            selectedTime = null
            morningAdapter.updateData(emptyList())
            afternoonAdapter.updateData(emptyList())
            Toast.makeText(
                this,
                "Doctor is not available on this day. Please select available working days.",
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

        // Fetch booked slots for this doctor and date
        database.child("Appointments").orderByChild("doctorUid").equalTo(doctorUid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val bookedSlots = mutableSetOf<String>()
                    for (postSnapshot in snapshot.children) {
                        val appointment = postSnapshot.getValue(com.example.medicare.Patient.models.AppointmentModel::class.java)
                        // Updated field names from refactored AppointmentModel
                        if (appointment != null && appointment.appointmentDate == selectedDate && appointment.status != "Cancelled") {
                            bookedSlots.add(appointment.appointmentTime)
                        }
                    }

                    val morningSlots = mutableListOf<TimeSlotModel>()
                    val afternoonSlots = mutableListOf<TimeSlotModel>()

                    val currentSlot = startTime.clone() as Calendar

                    while (currentSlot.before(endTime)) {
                        val time = sdf.format(currentSlot.time)
                        val isAvailable = !bookedSlots.contains(time)

                        val hour = currentSlot.get(Calendar.HOUR_OF_DAY)
                        if (hour < 12) {
                            morningSlots.add(TimeSlotModel(time, isAvailable = isAvailable, type = SlotType.MORNING))
                        } else {
                            afternoonSlots.add(TimeSlotModel(time, isAvailable = isAvailable, type = SlotType.AFTERNOON))
                        }

                        currentSlot.add(Calendar.MINUTE, duration)
                    }

                    morningAdapter.updateData(morningSlots)
                    afternoonAdapter.updateData(afternoonSlots)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}
