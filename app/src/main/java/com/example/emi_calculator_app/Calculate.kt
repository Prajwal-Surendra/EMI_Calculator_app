package com.example.emi_calculator_app

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import com.example.emi_calculator_app.modals.ListModal
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Calculate : AppCompatActivity() {
    private lateinit var prinamt: EditText
    private lateinit var downamt: EditText
    private lateinit var intrate: EditText
    private lateinit var loanterm: EditText
    private lateinit var calculateemi: Button
    private lateinit var txtresult: TextView
    private lateinit var startEMI: Button
    private lateinit var db : FirebaseFirestore
    private var result : Float = 0.0f
    private lateinit var rdb : DatabaseReference

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculate)
        prinamt = findViewById<EditText>(R.id.edpa)
        downamt = findViewById<EditText>(R.id.eddp)
        intrate = findViewById<EditText>(R.id.edir)
        loanterm = findViewById<EditText>(R.id.edlt)
        calculateemi = findViewById<AppCompatButton>(R.id.btnResult)
        startEMI = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.EMI_Start)
        txtresult = findViewById(R.id.tvResult)
        db= FirebaseFirestore.getInstance()
        val sharedPref = this?.getSharedPreferences("Email", Context.MODE_PRIVATE) ?: return
        val shared = this?.getSharedPreferences("EMI_title", Context.MODE_PRIVATE) ?: return
        var EMI_title = shared.getString("EMI_title", "1")
        var Email = sharedPref.getString("Email","1")
        rdb = FirebaseDatabase.getInstance().getReference("User")
        calculateemi.setOnClickListener {
            val principalAmount = prinamt.text.toString().toFloat()
            val downPayment = downamt.text.toString().toFloat()
            val interestRate = intrate.text.toString().toFloat()
            val loanTerm = loanterm.text.toString().toInt()
            if(checking(prinamt.text.toString(), downamt.text.toString(),intrate.text.toString(),loanterm.text.toString())) {
                try {
                    result = carcal(principalAmount, downPayment, interestRate, loanTerm)
                    txtresult.text = "Monthly EMI in Rs is: $result"
                } catch (e: RemoteException) {
                    e.printStackTrace()
                }
            }
            else
            {
                Toast.makeText(this,"Enter the details",Toast.LENGTH_LONG).show()
            }
        }
        startEMI.setOnClickListener {
            var currentDate: String = current_date()
            var incrementedDate: String = incremented_date(loanterm.text.toString().toLong())
            var user = mapOf( //Upload to database
                "EMI title" to EMI_title,
                "EMI Start date" to currentDate,
                "EMI End date" to incrementedDate,
                "Amount" to result.toString()
            )
            if (EMI_title != null) {
                saveDetails(EMI_title,currentDate,incrementedDate,result)
            }
            val Users = db.collection("USERS")
            if (!Email.isNullOrEmpty()) {
                val documentReference =
                    EMI_title?.let { it1 -> Users.document(Email).collection("List").document(it1) }
                documentReference?.set(user as Map<String, String>)?.addOnSuccessListener {
                    // Document successfully updated
                    val intent = Intent(this, User_List::class.java)
                    intent.putExtra("Email",Email)
                    startActivity(intent)
                }?.addOnFailureListener { e ->
                    // Error handling
                    Toast.makeText(this, "Failed to update document: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } else {
                // Handle the case when the email is null or empty
                Toast.makeText(this, "Invalid email", Toast.LENGTH_LONG).show()
            }
        }
    }
    fun carcal(pa: Float, dp: Float, ir: Float, lt: Int): Float {
        var emiamount: Float
        val principalamt = pa - dp // Subtract downpayment from principal amount
        val interest = ir / 12 / 100 // Convert interest for per month and divide it by 100 to get a number

        emiamount = (principalamt * interest * Math.pow(1 + interest.toDouble(), lt.toDouble()) / (Math.pow(
            1 + interest.toDouble(),
            lt.toDouble()
        ) - 1)).toFloat()

        return emiamount
    }
    private fun checking(pa:String ,dp: String, ir : String, lt : String): Boolean
    {
        if(pa.trim{it<=' '}.isNotEmpty() && dp.trim{it<=' '}.isNotEmpty() && ir.trim{it<=' '}.isNotEmpty() && lt.trim{it<=' '}.isNotEmpty()) {
            return true
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun current_date(): String { //Function to calculate current date
        val currentDate = LocalDate.now()
        val year = currentDate.year
        val month = currentDate.monthValue
        val dayOfMonth = currentDate.dayOfMonth
        return "$year-$month-$dayOfMonth"
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun incremented_date(value: Long): String { //Function to calculate the incremented date
        val currentDate = LocalDate.now()
        val incrementedDate = currentDate.plusMonths(value) // Increment by value months
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val incrementedDateFormatted = incrementedDate.format(formatter)
        return incrementedDateFormatted
    }
    private fun saveDetails(EMI_title : String, current_data : String, increment_date: String, result: Float) { //Function to upload the details to My list page using Real time database
        val eid = rdb.push().key!!
        val lil = ListModal(eid, EMI_title, current_data, increment_date, result)
        rdb.child(eid).setValue(lil)
            .addOnSuccessListener {
                Toast.makeText(this,"Value Inserted",Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {err->
                Toast.makeText(this,"Error ${err.message}",Toast.LENGTH_LONG).show()

            }
    }
}

