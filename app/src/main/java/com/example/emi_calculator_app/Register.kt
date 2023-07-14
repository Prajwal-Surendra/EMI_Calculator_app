package com.example.emi_calculator_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Register : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth //Firebase Authentication
    private lateinit var db : FirebaseFirestore //Firebase FireStore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        var name = findViewById<EditText>(R.id.edName)
        var email = findViewById<EditText>(R.id.edEmail)
        var password = findViewById<EditText>(R.id.edPassword)
        findViewById<Button>(R.id.btnRegister).setOnClickListener {
            if(checking(name.text.toString(),email.text.toString(),password.text.toString())) { //Checks if all the details have been entered or not
                var Name = name.text
                var Email = email.text
                val user = hashMapOf( // Creating hash map to upload the values to the firestore database
                    "Name" to Name.toString(),
                    "Email" to Email.toString(),
                )
                val Users = db.collection("USERS")
                val query = Users.whereEqualTo("Email",Email.toString()).get()//Checks if user has already registered or not
                    .addOnSuccessListener {
                            it->
                        if(it.isEmpty){ //New user
                            auth.createUserWithEmailAndPassword(Email.toString(),password.text.toString())
                                .addOnCompleteListener {
                                        task->
                                    if(task.isSuccessful)
                                    { //Upload to Firestore Database
                                        Users.document(Email.toString()).set(user)
                                        val intent = Intent(this,Home::class.java)
                                        intent.putExtra("Email",Email.toString())
                                        startActivity(intent)
                                        finish()
                                    }
                                    else{
                                        Toast.makeText(this,"Authentication Failed", Toast.LENGTH_LONG).show()
                                    }
                                }
                        }
                        else { //Redirect to login page
                            Toast.makeText(this,"User already registered", Toast.LENGTH_LONG).show()
                            val intent = Intent(this,MainActivity::class.java)
                            startActivity(intent)
                        }
                    }
            }
            else {
                Toast.makeText(this,"Enter the details", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun checking(name:String, email: String, pass : String): Boolean{
        if(name.trim{it<=' '}.isNotEmpty() && email.trim{it<=' '}.isNotEmpty() && pass.trim{it<=' '}.isNotEmpty()) {
            return true
        }
        return false
    }
}