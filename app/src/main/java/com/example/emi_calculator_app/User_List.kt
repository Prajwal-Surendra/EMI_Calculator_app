package com.example.emi_calculator_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.emi_calculator_app.adapter.Adapter
import com.example.emi_calculator_app.modals.ListModal
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class User_List : AppCompatActivity() {
    private lateinit var recycler_View : RecyclerView
    private lateinit var userlist: ArrayList<ListModal>
    private lateinit var rdb : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)
        recycler_View = findViewById(R.id.recycler_view)
        recycler_View.layoutManager= LinearLayoutManager(this)
        recycler_View.setHasFixedSize(true)

        userlist = arrayListOf<ListModal>()
        getuserData()
    }
    private fun getuserData() {
        recycler_View.visibility = View.GONE
        rdb = FirebaseDatabase.getInstance().getReference("User")
        rdb.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userlist.clear()
                if(snapshot.exists()){
                    for(emi in snapshot.children){
                        val userData= emi.getValue(ListModal::class.java)
                        userlist.add(userData!!)
                    }
                    val mAdapter = Adapter(userlist)
                    recycler_View.adapter = mAdapter
                    recycler_View.visibility= View.VISIBLE
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}

