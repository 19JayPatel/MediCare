package com.example.medicare.Admin.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medicare.Admin.adapter.UsersAdapter
import com.example.medicare.Admin.model.UserModel
import com.example.medicare.R
import com.example.medicare.databinding.FragmentAdminUsersBinding

class AdminUsersFragment : Fragment() {

    private var _binding: FragmentAdminUsersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val users = listOf(
            UserModel("Sarah Johnson", "sarah.j@gmail.com", "Jan 12, 2024", 12, R.drawable.ic_user),
            UserModel("Rahul Mehta", "rahul.m@gmail.com", "Mar 5, 2024", 7, R.drawable.ic_user),
            UserModel("Priya Sharma", "priya.s@gmail.com", "Feb 18, 2024", 3, R.drawable.ic_user),
            UserModel("Anil Kumar", "anil.k@gmail.com", "Apr 2, 2024", 5, R.drawable.ic_user),
            UserModel("Neha Patel", "neha.p@gmail.com", "Dec 30, 2023", 9, R.drawable.ic_user),
            UserModel("Vikram Singh", "vikram.s@gmail.com", "May 14, 2024", 1, R.drawable.ic_user)
        )

        binding.rvUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUsers.adapter = UsersAdapter(users)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
