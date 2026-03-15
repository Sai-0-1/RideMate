package com.example.ridemate

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RideAdapter(private val rides: List<Ride>) :
    RecyclerView.Adapter<RideAdapter.RideViewHolder>() {

    inner class RideViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val destination: TextView = view.findViewById(R.id.tvDestination)
        val driverName: TextView = view.findViewById(R.id.tvDriverName)
        val time: TextView = view.findViewById(R.id.tvTime)
        val seats: TextView = view.findViewById(R.id.tvSeats)
        val vehicle: TextView = view.findViewById(R.id.tvVehicle)
        val requestBtn: Button = view.findViewById(R.id.btnRequest)
        val chatBtn: Button = view.findViewById(R.id.btnChat)
        val editBtn: Button = view.findViewById(R.id.btnEdit)
        val deleteBtn: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ride, parent, false)
        return RideViewHolder(view)
    }

    override fun onBindViewHolder(holder: RideViewHolder, position: Int) {
        val ride = rides[position]
        val context = holder.itemView.context
        val db = FirebaseFirestore.getInstance()
        val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        holder.destination.text = ride.destination
        holder.driverName.text = "Driver: ${ride.driverName}"
        holder.time.text = "Time: ${ride.time}"
        holder.seats.text = "Seats: ${ride.seats}"
        holder.vehicle.text = "Vehicle: ${ride.vehicle}"

        if (ride.driverId == currentUid) {
            holder.editBtn.visibility = View.VISIBLE
            holder.deleteBtn.visibility = View.VISIBLE
            holder.requestBtn.visibility = View.GONE
        } else {
            holder.editBtn.visibility = View.GONE
            holder.deleteBtn.visibility = View.GONE
            holder.requestBtn.visibility = View.VISIBLE
        }

        holder.requestBtn.setOnClickListener {
            val request = hashMapOf(
                "rideId" to ride.id,
                "passengerId" to currentUid,
                "driverId" to ride.driverId,
                "status" to "pending"
            )
            db.collection("requests").add(request)
                .addOnSuccessListener {
                    Toast.makeText(context, "Ride requested!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to request", Toast.LENGTH_SHORT).show()
                }
        }

        holder.chatBtn.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("rideId", ride.id)
            intent.putExtra("driverId", ride.driverId)
            context.startActivity(intent)
        }

        holder.editBtn.setOnClickListener {
            val intent = Intent(context, PostRideActivity::class.java)
            intent.putExtra("rideId", ride.id)
            intent.putExtra("destination", ride.destination)
            intent.putExtra("time", ride.time)
            intent.putExtra("seats", ride.seats.toString())
            intent.putExtra("vehicle", if (ride.vehicle == "Not specified") "" else ride.vehicle)
            context.startActivity(intent)
        }

        holder.deleteBtn.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Delete Ride")
                .setMessage("Are you sure you want to delete this ride?")
                .setPositiveButton("Delete") { _, _ ->
                    db.collection("rides").document(ride.id)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(context, "Ride deleted!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun getItemCount() = rides.size
}