package com.example.emi_calculator_app

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Profile : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var auth : FirebaseAuth
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val email = findViewById<TextView>(R.id.tvEmailDisplay)
        val name = findViewById<TextView>(R.id.tvNameDisplay)

        val sharedPref = this?.getSharedPreferences("Email",Context.MODE_PRIVATE) ?: return
        var emailString = sharedPref.getString("Email","1")
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser.toString()
        db = FirebaseFirestore.getInstance()
                if (emailString != null) {
                    db.collection("USERS").document(emailString).get()
                        .addOnSuccessListener { tasks ->
                            email.text = emailString
                            name.text = tasks.getString("Name") ?: "Unknown"
                        }
                        .addOnFailureListener { e ->
                            // Error handling
                            Toast.makeText(this, "Failed to retrieve user information: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }
            }
        }

