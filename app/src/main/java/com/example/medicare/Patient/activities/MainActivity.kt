package com.example.medicare.Patient.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.medicare.Patient.fragments.MyBookingsFragment
import com.example.medicare.Patient.fragments.PatientHomeFragment
import com.example.medicare.Patient.fragments.PatientMapFragment
import com.example.medicare.Patient.fragments.PatientProfileFragment
import com.example.medicare.R
import com.example.medicare.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Default Fragment
        replaceFragment(PatientHomeFragment())

        // Bottom Navigation Listener
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(PatientHomeFragment())
                    true
                }
                R.id.nav_location -> {
                    replaceFragment(PatientMapFragment())
                    true
                }
                R.id.nav_calendar -> {
                    replaceFragment(MyBookingsFragment())
                    true
                }
                R.id.nav_profile -> {
                    replaceFragment(PatientProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    // Function to replace fragments
    fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }

    // Helper function to go back to Home tab
    fun navigateToHome() {
        binding.bottomNav.selectedItemId = R.id.nav_home
    }
}