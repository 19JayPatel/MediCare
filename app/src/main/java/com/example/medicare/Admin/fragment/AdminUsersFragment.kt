package com.example.medicare.Admin.fragment

import android.os.Bundle
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

class AdminUsersFragment : Fragment() {

    private var _binding: FragmentAdminUsersBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private val userList = mutableListOf<UserModel>()
    private lateinit var adapter: UsersAdapter

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
        fetchUsers()
    }

    private fun setupRecyclerView() {
        adapter = UsersAdapter(userList)
        binding.rvUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUsers.adapter = adapter
    }

    private fun fetchUsers() {
        database.child("users").orderByChild("role").equalTo("Patient")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userList.clear()
                    for (postSnapshot in snapshot.children) {
                        // Map Firebase 'fullName' to Model 'name' if necessary or use specialized mapping
                        val name = postSnapshot.child("fullName").value as? String ?: ""
                        val email = postSnapshot.child("email").value as? String ?: ""
                        val joinedDate = postSnapshot.child("joinedDate").value as? String ?: "Jan 2024"
                        
                        val user = UserModel(
                            name = name,
                            email = email,
                            joinedDate = joinedDate,
                            bookingCount = (postSnapshot.child("bookingCount").value as? Long)?.toInt() ?: 0,
                            imageRes = R.drawable.ic_user,
                            userId = postSnapshot.key ?: ""
                        )
                        userList.add(user)
                    }
                    
                    if (userList.isEmpty()) {
                        // Fallback to static data if no users in DB
                        userList.addAll(listOf(
                            UserModel("Sarah Johnson", "sarah.j@gmail.com", "Jan 12, 2024", 12, R.drawable.ic_user),
                            UserModel("Rahul Mehta", "rahul.m@gmail.com", "Mar 5, 2024", 7, R.drawable.ic_user),
                            UserModel("Priya Sharma", "priya.s@gmail.com", "Feb 18, 2024", 3, R.drawable.ic_user)
                        ))
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to load users", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
