package com.example.medicare.utils

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import com.example.medicare.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth

object LogoutHelper {

    fun showLogoutDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout?")
        
        builder.setPositiveButton("Yes") { dialog, _ ->
            performLogout(context)
            dialog.dismiss()
        }
        
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        
        val dialog = builder.create()
        dialog.show()
    }

    private fun performLogout(context: Context) {
        // Sign out from Firebase
        FirebaseAuth.getInstance().signOut()
        
        // Clear Local Session
        val sessionManager = SessionManager(context)
        sessionManager.clearSession()
        
        // Redirect to Login and clear backstack
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }
}
