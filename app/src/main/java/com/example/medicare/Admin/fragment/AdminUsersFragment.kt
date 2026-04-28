package com.example.medicare.Admin.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicare.Admin.adapter.UsersAdapter
import com.example.medicare.Admin.model.UserModel
import com.example.medicare.R
import com.example.medicare.databinding.FragmentAdminUsersBinding
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class AdminUsersFragment : Fragment() {

    private var _binding: FragmentAdminUsersBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private val userList = mutableListOf<UserModel>()
    private val filteredList = mutableListOf<UserModel>()
    private lateinit var adapter: UsersAdapter
    
    // Map to store booking counts: PatientUid -> Count
    private val bookingCountsMap = mutableMapOf<String, Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminUsersBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance().reference
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()
        fetchBookingsAndUsers()
    }

    private fun setupRecyclerView() {
        adapter = UsersAdapter(filteredList)
        binding.rvUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUsers.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterUsers(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterUsers(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(userList)
        } else {
            val lowerCaseQuery = query.lowercase()
            for (user in userList) {
                if (user.name.lowercase().contains(lowerCaseQuery) ||
                    user.email.lowercase().contains(lowerCaseQuery)
                ) {
                    filteredList.add(user)
                }
            }
        }
        binding.tvLabel.text = "REGISTERED USERS (${filteredList.size})"
        adapter.notifyDataSetChanged()
    }

    private fun fetchBookingsAndUsers() {
        // First, count appointments for each patient using patientUid from Appointments node
        database.child("Appointments").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bookingCountsMap.clear()
                for (appointmentSnap in snapshot.children) {
                    val patientUid = appointmentSnap.child("patientUid").value as? String ?: ""
                    if (patientUid.isNotEmpty()) {
                        val currentCount = bookingCountsMap.getOrDefault(patientUid, 0)
                        bookingCountsMap[patientUid] = currentCount + 1
                    }
                }
                // Once bookings are fetched, load users
                fetchUsers()
            }

            override fun onCancelled(error: DatabaseError) {
                fetchUsers()
            }
        })
    }

    private fun fetchUsers() {
        database.child("users").orderByChild("role").equalTo("Patient")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (_binding == null) return
                    userList.clear()
                    for (postSnapshot in snapshot.children) {
                        val uid = postSnapshot.key ?: ""
                        val name = postSnapshot.child("fullName").value as? String ?: ""
                        val email = postSnapshot.child("email").value as? String ?: ""
                        
                        // Handle dynamic Joined Date
                        var joinedDate = postSnapshot.child("joinedDate").value as? String
                        
                        // Fallback to createdAt if joinedDate is missing
                        if (joinedDate == null || joinedDate == "Not Available") {
                            val createdAt = postSnapshot.child("createdAt").value as? Long
                            if (createdAt != null) {
                                val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                joinedDate = sdf.format(Date(createdAt))
                            } else {
                                joinedDate = "" // Empty string instead of "Not Available"
                            }
                        }
                        
                        val imageUrl = postSnapshot.child("imageUrl").value as? String ?: ""
                        val bookingCount = bookingCountsMap.getOrDefault(uid, 0)
                        
                        val user = UserModel(
                            name = name,
                            email = email,
                            joinedDate = joinedDate!!,
                            bookingCount = bookingCount,
                            imageRes = R.drawable.ic_user,
                            imageUrl = imageUrl,
                            userId = uid
                        )
                        userList.add(user)
                    }
                    
                    filterUsers(binding.etSearch.text.toString())
                }

                override fun onCancelled(error: DatabaseError) {
                    if (context != null) {
                        Toast.makeText(context, "Failed to load users", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
