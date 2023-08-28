package com.example.bouncy

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.FirebaseDatabase

class AuthorizationActivity : AppCompatActivity() {

    lateinit var textEmailField: TextInputEditText
    lateinit var textPasswordField: TextInputEditText

    lateinit var auth: FirebaseAuth

    public override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if (currentUser != null && !currentUser.isAnonymous) {

            Toast.makeText(
                this,
                "Logged as ${currentUser.email}",
                Toast.LENGTH_SHORT,
            ).show()
            val intent = Intent(applicationContext, LogOutActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorization)
        auth = FirebaseAuth.getInstance()
        textEmailField = findViewById(R.id.email_field)
        textPasswordField = findViewById(R.id.password_field)

    }

    fun login_user(view: View) {
        var email = textEmailField.text.toString()
        var password = textPasswordField.text.toString()

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, email, Toast.LENGTH_SHORT,).show()
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val errorCode = (task.exception as FirebaseAuthException).errorCode
                    if (errorCode == "ERROR_USER_NOT_FOUND") {
                        Toast.makeText(
                            this, "email doesn't exists", Toast.LENGTH_SHORT,
                        ).show()
                    } else if (errorCode == "ERROR_WRONG_PASSWORD") {
                            Toast.makeText(
                                this, "wrong password", Toast.LENGTH_SHORT,
                            ).show()
                    } else{
                        Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT,).show()
                    }

                }
            }
    }

    fun register_user(view: View) {
        var email = textEmailField.text.toString()
        var password = textPasswordField.text.toString()

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
            return
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter password", Toast.LENGTH_SHORT).show()
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    val currentUser = auth.currentUser
                    val userId = currentUser!!.uid
                    val user = User(userId, 0, email)

                    val database =
                        FirebaseDatabase.getInstance("https://bouncygame-99f87-default-rtdb.europe-west1.firebasedatabase.app")
                            .getReference("users")

                    database.child(userId).setValue(user).addOnSuccessListener {
                            Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this,
                                "Failed to save data: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                } else {

                    val errorCode = (task.exception as FirebaseAuthException).errorCode

                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    if (errorCode == "ERROR_EMAIL_ALREADY_IN_USE"){
                        Toast.makeText(
                            this, "email already exists", Toast.LENGTH_SHORT,
                        ).show()

                    } else if (errorCode == "ERROR_INVALID_EMAIL") {
                        Toast.makeText(
                            this, "invalid email", Toast.LENGTH_SHORT,
                        ).show()
                    } else if (errorCode == "ERROR_WEAK_PASSWORD") {
                        Toast.makeText(
                            this, "Password should be at least 6 charcters", Toast.LENGTH_SHORT,
                        ).show()
                    } else{
                        Toast.makeText(
                            this,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }

                }
            }
    }

    fun back(view: View) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}







































