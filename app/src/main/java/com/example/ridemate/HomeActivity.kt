package com.example.ridemate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RideAdapter
    private val rideList = mutableListOf<Ride>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        db = FirebaseFirestore.getInstance()

        recyclerView = findViewById(R.id.rvRides)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = RideAdapter(rideList)
        recyclerView.adapter = adapter

        findViewById<Button>(R.id.btnPostRide).setOnClickListener {
            startActivity(Intent(this, PostRideActivity::class.java))
        }

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        loadRides()
    }

    private fun loadRides() {
        db.collection("rides")
            .whereEqualTo("status", "active")
            .addSnapshotListener { result, error ->
                if (error != null) {
                    Toast.makeText(this, "Failed to load rides", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                rideList.clear()
                for (doc in result!!) {
                    val ride = Ride(
                        id = doc.id,
                        driverId = doc.getString("driverId") ?: "",
                        driverName = doc.getString("driverName") ?: "Unknown",
                        destination = doc.getString("destination") ?: "",
                        time = doc.getString("time") ?: "",
                        seats = (doc.getLong("seats") ?: 0).toInt(),
                        // This line fixes vehicle not showing
                        vehicle = doc.getString("vehicle") ?: "Not specified",
                        status = doc.getString("status") ?: "active"
                    )
                    rideList.add(ride)
                }
                adapter.notifyDataSetChanged()
            }
    }
}