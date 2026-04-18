package com.example.medicare.Admin.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.medicare.Admin.fragment.AdminDashboardFragment
import com.example.medicare.Admin.fragment.AdminDoctorsFragment
import com.example.medicare.Admin.fragment.AdminProfileFragment
import com.example.medicare.Admin.fragment.AdminUsersFragment
import com.example.medicare.R
import com.example.medicare.databinding.AdminBottomNavBinding

class AdminMainActivity : AppCompatActivity() {

    private lateinit var binding: AdminBottomNavBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AdminBottomNavBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set default fragment
        replaceFragment(AdminDashboardFragment()) 

        binding.adminBottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.admin_nav_home -> {
                    replaceFragment(AdminDashboardFragment())
                    true
                }
                R.id.admin_nav_doctors -> {
                    replaceFragment(AdminDoctorsFragment())
                    true
                }
                R.id.admin_nav_users -> {
                    replaceFragment(AdminUsersFragment())
                    true
                }
                R.id.admin_nav_profile -> {
                    replaceFragment(AdminProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.adminFragmentContainer, fragment)
            .commit()
    }
}