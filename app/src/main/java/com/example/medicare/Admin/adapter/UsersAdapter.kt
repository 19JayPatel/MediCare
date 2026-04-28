package com.example.medicare.Admin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medicare.Admin.model.UserModel
import com.example.medicare.R
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
            tvJoinedDate.text = user.joinedDate
            tvBookingCount.text = user.bookingCount.toString()
            
            if (user.imageUrl.isNotEmpty()) {
                Glide.with(holder.itemView.context)
                    .load(user.imageUrl)
                    .placeholder(R.drawable.ic_user)
                    .error(R.drawable.ic_user)
                    .circleCrop()
                    .into(ivUser)
            } else {
                ivUser.setImageResource(R.drawable.ic_user)
            }
        }
    }

    override fun getItemCount(): Int = users.size
}
