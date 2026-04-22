package com.example.medicare.Patient.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medicare.Patient.adapter.TimeSlotAdapter
import com.example.medicare.Patient.model.SlotType
import com.example.medicare.Patient.model.TimeSlotModel
import com.example.medicare.R
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*

class SelectTimeSlotActivity : AppCompatActivity() {

    private lateinit var morningAdapter: TimeSlotAdapter
    private lateinit var afternoonAdapter: TimeSlotAdapter
    private var selectedDate: String = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
    private var selectedTime: String? = null

    private var doctorName: String? = null
    private var specialization: String? = null
    private var hospital: String? = null
    private var doctorId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_time_slot)

        // Get Data from Intent
        doctorName = intent.getStringExtra("doctorName")
        specialization = intent.getStringExtra("specialization")
        hospital = intent.getStringExtra("hospital")
        doctorId = intent.getStringExtra("doctorId")

        setupUI()
        setupAdapters()
        setupCalendar()

        findViewById<MaterialButton>(R.id.btnConfirmBooking).setOnClickListener {
            if (selectedTime == null) {
                Toast.makeText(this, "Please select a time slot", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, BookingConfirmationActivity::class.java)
                intent.putExtra("doctorName", doctorName)
                intent.putExtra("selectedDate", selectedDate)
                intent.putExtra("selectedTime", selectedTime)
                startActivity(intent)
            }
        }

        findViewById<android.view.View>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    private fun setupUI() {
        findViewById<TextView>(R.id.tvDoctorName).text = doctorName
        findViewById<TextView>(R.id.tvDoctorDetails).text = "$specialization • $hospital"
    }

    private fun setupAdapters() {
        val morningSlots = listOf(
            TimeSlotModel("09:00 AM", type = SlotType.MORNING),
            TimeSlotModel("09:30 AM", type = SlotType.MORNING),
            TimeSlotModel("10:00 AM", type = SlotType.MORNING),
            TimeSlotModel("10:30 AM", isAvailable = false, type = SlotType.MORNING),
            TimeSlotModel("11:00 AM", type = SlotType.MORNING),
            TimeSlotModel("11:30 AM", type = SlotType.MORNING)
        )

        val afternoonSlots = listOf(
            TimeSlotModel("02:00 PM", type = SlotType.AFTERNOON),
            TimeSlotModel("02:30 PM", type = SlotType.AFTERNOON),
            TimeSlotModel("03:00 PM", isAvailable = false, type = SlotType.AFTERNOON)
        )

        morningAdapter = TimeSlotAdapter(morningSlots) { slot ->
            selectedTime = slot.time
            afternoonAdapter.clearSelection()
        }

        afternoonAdapter = TimeSlotAdapter(afternoonSlots) { slot ->
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
        val calendarView = findViewById<android.widget.CalendarView>(R.id.calendarView)
        
        // Fix: CalendarView crash by ensuring minDate is set correctly
        val currentTime = System.currentTimeMillis()
        // Set maxDate far in the future first to avoid minDate > maxDate collision
        calendarView.maxDate = currentTime + (1000L * 60 * 60 * 24 * 365 * 10) // 10 years
        // Use a small offset for minDate
        calendarView.minDate = currentTime - 1000 

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            selectedDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(calendar.time)
        }
    }
}
