package com.example.emi_calculator_app

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.firestore.FirebaseFirestore

class Home : AppCompatActivity() {
    private lateinit var db : FirebaseFirestore
    private lateinit var toggle : ActionBarDrawerToggle
    var Email: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)
        var drawerlayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        var nav = findViewById<NavigationView>(R.id.nav_view)
        val sharedPref = this?.getSharedPreferences("Email",Context.MODE_PRIVATE)?:return // Shared Preferences
        val isLogin = sharedPref.getString("Email","1")
        db= FirebaseFirestore.getInstance()
        Email = intent.getStringExtra("Email").toString()
        var emi_title = findViewById<EditText>(R.id.EMI_title)
        var calculator= findViewById<ImageView>(R.id.btnCalculate)
        if(isLogin == "1")
        {
            if(Email!=null)
            {
                setText(Email)
                with(sharedPref.edit())
                {
                    putString("Email",Email)
                    apply()
                }
            }
            else{
                val intent= Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        else
        {
            setText(isLogin)
        }
        toggle = ActionBarDrawerToggle(this,drawerlayout,R.string.open,R.string.close)
        drawerlayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        nav.setNavigationItemSelectedListener {//Navigation Drawer
            when(it.itemId) {
                R.id.Home -> {
                    val intent= Intent(this,Home::class.java)
                    startActivity(intent)
                }
                R.id.Profile -> {
                    val intent = Intent (this, Profile::class.java)
                    intent.putExtra("Email",Email)
                    startActivity(intent)
                }
                R.id.List -> {
                    val intent = Intent (this, User_List::class.java)
                    intent.putExtra("Email",Email)
                    startActivity(intent)
                }
                R.id.Logout -> {
                    sharedPref.edit().remove("Email").apply()
                    val intent= Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            true
        }

        calculator.setOnClickListener {
            if (emi_title.text.isNullOrBlank()) {
                Toast.makeText(this, "Enter the EMI title", Toast.LENGTH_LONG).show()
            } else {
                val Users = db.collection("USERS")
                val user = mapOf( //Upload EMI title to the firestore database
                    "EMI title" to emi_title.text.toString()
                )
                if (!isLogin.isNullOrEmpty()) {
                    val documentReference = Users.document(isLogin)
                    documentReference.update(user as Map<String, String>)
                        .addOnSuccessListener {
                            // Document successfully updated
                            val intent = Intent(this, Calculate::class.java)
                            intent.putExtra("Email",Email)
                            intent.putExtra("EMI_title",emi_title.text.toString())
                            startActivity(intent)
                        }
                        .addOnFailureListener { e ->
                            // Error handling
                            Toast.makeText(this, "Failed to update document: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                } else {
                    // Handle the case when the email is null or empty
                    Toast.makeText(this, "Invalid email", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    private fun setText(email:String?){
        val name = findViewById<TextView>(R.id.tvName)
        db=FirebaseFirestore.getInstance()
        if (email != null) {
            db.collection("USERS").document(email).get()
                .addOnSuccessListener { tasks->
                    name.text=tasks.get("Name").toString()
                }
        }
    }
}