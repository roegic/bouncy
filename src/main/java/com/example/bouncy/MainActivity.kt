package com.example.bouncy

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        fetch_data()
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.main_playButton -> {
                val intent = Intent(applicationContext, GameActivity::class.java)
                startActivity(intent)
                finish()
            }

            R.id.main_authorizationButton -> {
                val intent = Intent(applicationContext, AuthorizationActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    fun fetch_data() {
        val currentUser = auth.currentUser

        var highScore = ""
        var score: TextView = findViewById(R.id.high_score_text_main)

        if (currentUser?.uid == null) {
            auth.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val currentUser = auth.currentUser
                        val userId = currentUser!!.uid
                        val user = User(userId, 0, "guest")
                        val database =
                            FirebaseDatabase.getInstance("https://bouncygame-99f87-default-rtdb.europe-west1.firebasedatabase.app")
                                .getReference("users")
                        database.child(userId).setValue(user)
                            .addOnSuccessListener {
                            }
                            .addOnFailureListener { e ->
                            }
                    } else {
                        Toast.makeText(baseContext, "Error", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        if (currentUser?.uid == null) {
            highScore = "log in to save score"
            score.text = highScore
            return 
        }

        var uid = currentUser.uid

        val database =
            FirebaseDatabase.getInstance("https://bouncygame-99f87-default-rtdb.europe-west1.firebasedatabase.app")
        val usersRef = database.getReference("users").child(uid)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val user = dataSnapshot.getValue(User::class.java)
                    if (user != null) {
                        highScore = user.high_score.toString()
                        score.text = highScore
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(baseContext, "Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

}