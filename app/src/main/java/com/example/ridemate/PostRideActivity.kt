package com.example.ridemate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PostRideActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var editRideId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_ride)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val titleTV = findViewById<TextView>(R.id.tvTitle)
        val destinationET = findViewById<EditText>(R.id.etDestination)
        val timeET = findViewById<EditText>(R.id.etTime)
        val seatsET = findViewById<EditText>(R.id.etSeats)
        val vehicleET = findViewById<EditText>(R.id.etVehicle)
        val postBtn = findViewById<Button>(R.id.btnPost)

        editRideId = intent.getStringExtra("rideId")

        if (editRideId != null) {
            titleTV.text = "Edit Ride"
            postBtn.text = "Update Ride"
            destinationET.setText(intent.getStringExtra("destination"))
            timeET.setText(intent.getStringExtra("time"))
            seatsET.setText(intent.getStringExtra("seats"))
            val existingVehicle = intent.getStringExtra("vehicle") ?: ""
            vehicleET.setText(
                if (existingVehicle == "Not specified") "" else existingVehicle
            )
        } else {
            titleTV.text = "Post a Ride"
            postBtn.text = "Post Ride"
        }

        postBtn.setOnClickListener {
            val destination = destinationET.text.toString().trim()
            val time = timeET.text.toString().trim()
            val seats = seatsET.text.toString().trim()
            val vehicle = vehicleET.text.toString().trim()

            if (destination.isEmpty() || time.isEmpty() || seats.isEmpty()) {
                Toast.makeText(this, "Fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Disable button immediately to prevent double tap
            postBtn.isEnabled = false
            postBtn.text = "Saving..."

            val uid = auth.currentUser!!.uid

            db.collection("users").document(uid).get()
                .addOnSuccessListener { userDoc ->
                    val driverName = userDoc.getString("name") ?: "Unknown"
                    val vehicleValue = if (vehicle.isEmpty()) "Not specified" else vehicle

                    val rideData = hashMapOf(
                        "driverId" to uid,
                        "driverName" to driverName,
                        "destination" to destination,
                        "time" to time,
                        "seats" to seats.toInt(),
                        "vehicle" to vehicleValue,
                        "status" to "active"
                    )

                    if (editRideId != null) {
                        // UPDATE
                        db.collection("rides").document(editRideId!!)
                            .update(rideData as Map<String, Any>)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "Ride updated!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                goToHome()
                            }
                            .addOnFailureListener {
                                postBtn.isEnabled = true
                                postBtn.text = "Update Ride"
                                Toast.makeText(
                                    this,
                                    "Update failed: ${it.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        // POST NEW
                        db.collection("rides").add(rideData)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "Ride posted!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                goToHome()
                            }
                            .addOnFailureListener {
                                postBtn.isEnabled = true
                                postBtn.text = "Post Ride"
                                Toast.makeText(
                                    this,
                                    "Failed: ${it.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
                .addOnFailureListener {
                    postBtn.isEnabled = true
                    Toast.makeText(this, "Could not get user info", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Separate function to go back to home — called after post or update
    private fun goToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }
}