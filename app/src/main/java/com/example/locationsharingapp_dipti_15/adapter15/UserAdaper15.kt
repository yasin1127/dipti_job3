package com.example.locationsharingapp_dipti_15.adapter15

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.locationsharingapp_dipti_15.databinding.ItemUser15Binding
import com.example.locationsharingapp_dipti_15.model15.User15

class UserAdaper15(private var userList: List<User15>) : RecyclerView.Adapter<UserAdaper15.UserViewHolder>() {

    class UserViewHolder(private val binding: ItemUser15Binding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User15) {
            binding.apply {
                displayNameTxt15.text = user.displayName
                emailTxt15.text = user.email
                locationTxt15.text = user.location
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            ItemUser15Binding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)
    }

    fun updateData(newList: List<User15>) {
        userList = newList
        notifyDataSetChanged()
    }
}
