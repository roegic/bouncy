package com.example.bouncy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class LogOutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_out)
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        val emailField: TextView = findViewById(R.id.user_details)
        emailField.setText(user?.email)
    }

    fun logOut(view: View){
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(applicationContext, AuthorizationActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun back(view: View) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}