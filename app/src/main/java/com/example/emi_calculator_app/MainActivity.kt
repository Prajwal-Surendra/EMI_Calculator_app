package com.example.emi_calculator_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth //Firebase Authentication
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        var email = findViewById<EditText>(R.id.edEmail)
        var password = findViewById<EditText>(R.id.edPassword)
        var login = findViewById<Button>(R.id.btnLogin)
        var register = findViewById<TextView>(R.id.Register)
        login. setOnClickListener {
            if(checking(email.text.toString(),password.text.toString())){ //Checks is user has entered details or not
                auth.signInWithEmailAndPassword(email.text.toString(),password.text.toString())
                    .addOnCompleteListener(){
                        task ->
                        if(task.isSuccessful){
                            val intent = Intent (this, Home::class.java) //Redirect to home page
                            intent.putExtra("Email",email.text.toString()) //Email used as primary key in the firestore database
                            startActivity(intent)
                            finish()
                        }
                        else{
                            Toast.makeText(this,"Incorrect Login Details",Toast.LENGTH_LONG).show() //Toast or incorrect login details
                        }
                }
            }
            else{
                Toast.makeText(this,"Enter the details",Toast.LENGTH_LONG).show()
            }
        }
        register.setOnClickListener{//Redirecting to Sign up page if new user
            val intent = Intent (this, Register::class.java)
            startActivity(intent)
        }
    }
    private fun checking(email:String, password: String ): Boolean{
    if(email.trim{it <= ' '}.isNotEmpty() && password.trim{it <= ' '}.isNotEmpty()) {
        return true
    }
        return false
    }
}