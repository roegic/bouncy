package com.example.bouncy

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GameOverActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    var points: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)
        var score: TextView = findViewById(R.id.score)
        points = intent.extras!!.getInt("points")
        score.text = points.toString()
        auth = FirebaseAuth.getInstance()
        update_data()
    }

    fun restart(view: View) {
        val intent = Intent(applicationContext, GameActivity::class.java)
        startActivity(intent)
        finish()
    }


    fun go_to_menu(view: View) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun update_data() {

        val currentUser = auth.currentUser
        val uid = currentUser?.uid
        val database =
            FirebaseDatabase.getInstance("https://bouncygame-99f87-default-rtdb.europe-west1.firebasedatabase.app")

        if (uid != null) {

            val usersRef = database.getReference("users").child(uid)

            usersRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val user = dataSnapshot.getValue(User::class.java)
                        if (user != null) {
                            val highScore = user.high_score
                            if (highScore < points) usersRef.child("high_score").setValue(points)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            })
        }

    }

}