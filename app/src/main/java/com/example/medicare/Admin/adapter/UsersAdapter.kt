package com.example.medicare.Admin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medicare.Admin.model.UserModel
import com.example.medicare.databinding.ItemUserBinding

class UsersAdapter(private val users: List<UserModel>) :
    RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    class UserViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.binding.apply {
            tvUserName.text = user.name
            tvEmail.text = user.email
            tvJoinedDate.text = "Joined: ${user.joinedDate}"
            tvBookingCount.text = user.bookingCount.toString()
            ivUser.setImageResource(user.imageRes)
        }
    }

    override fun getItemCount(): Int = users.size
}
