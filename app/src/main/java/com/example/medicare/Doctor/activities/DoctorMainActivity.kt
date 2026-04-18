package com.example.medicare.Doctor.activities

import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.medicare.Doctor.fragment.DoctorAppointmentsFragment
import com.example.medicare.Doctor.fragment.DoctorDashboardFragment
import com.example.medicare.Doctor.fragment.DoctorProfileFragment
import com.example.medicare.Doctor.fragment.DoctorScheduleFragment
import com.example.medicare.R
import com.example.medicare.databinding.DoctorBottomNavigationBinding

class DoctorMainActivity : AppCompatActivity() {

    private lateinit var binding: DoctorBottomNavigationBinding
    private lateinit var navIcons: List<ImageView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DoctorBottomNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBottomNavigation()

        // Set default fragment
        if (savedInstanceState == null) {
            selectNavigationItem(binding.includeBottomNav.navHome, DoctorDashboardFragment())
        }
    }

    private fun setupBottomNavigation() {
        navIcons = listOf(
            binding.includeBottomNav.navHome,
            binding.includeBottomNav.navAppointments,
            binding.includeBottomNav.navSchedule,
            binding.includeBottomNav.navProfile
        )

        binding.includeBottomNav.navHome.setOnClickListener {
            selectNavigationItem(it as ImageView, DoctorDashboardFragment())
        }

        binding.includeBottomNav.navAppointments.setOnClickListener {
            selectNavigationItem(it as ImageView, DoctorAppointmentsFragment())
        }

        binding.includeBottomNav.navSchedule.setOnClickListener {
            selectNavigationItem(it as ImageView, DoctorScheduleFragment())
        }

        binding.includeBottomNav.navProfile.setOnClickListener {
            selectNavigationItem(it as ImageView, DoctorProfileFragment())
        }
    }

    private fun selectNavigationItem(selectedIcon: ImageView, fragment: Fragment) {
        // Reset all icons to unselected color
        navIcons.forEach { icon ->
            icon.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(this, R.color.text_secondary)
            )
        }

        // Set selected icon color
        selectedIcon.imageTintList = ColorStateList.valueOf(
            ContextCompat.getColor(this, R.color.primary)
        )

        replaceFragment(fragment)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.doctorFragmentContainer, fragment)
            .commit()
    }
}
