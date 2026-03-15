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

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val nameET = findViewById<EditText>(R.id.etName)
        val emailET = findViewById<EditText>(R.id.etEmail)
        val passwordET = findViewById<EditText>(R.id.etPassword)
        val registerBtn = findViewById<Button>(R.id.btnRegister)
        val goLogin = findViewById<TextView>(R.id.tvGoLogin)

        registerBtn.setOnClickListener {
            val name = nameET.text.toString().trim()
            val email = emailET.text.toString().trim()
            val pass = passwordET.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!email.endsWith("@cvr.ac.in")) {
                Toast.makeText(this, "Use your CVR college email!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener { result ->
                    val uid = result.user!!.uid
                    val user = hashMapOf(
                        "uid" to uid,
                        "name" to name,
                        "email" to email
                    )
                    db.collection("users").document(uid).set(user)
                        .addOnSuccessListener {
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        goLogin.setOnClickListener { finish() }
    }
}