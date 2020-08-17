package com.cploutarchou.facebookdeeplinking

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val tag = "MyMessage"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

        btn_sign_up.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    SignUpActivity::class.java
                )
            )
            finish()
        }

        btn_login.setOnClickListener {
            doLogin()
        }


    }

    private fun doLogin() {
        if (login_username.text.toString().isEmpty()) {
            login_username.error = "Please enter your email address"
            login_username.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(login_username.text.toString()).matches()) {
            login_username.error = "Please enter a valid email address"
            login_username.requestFocus()
            return
        }

        if (login_password.text.toString().isEmpty()) {
            login_password.error = "Please enter a valid email address"
            login_password.requestFocus()
            return
        }
        auth.signInWithEmailAndPassword(
            login_username.text.toString(),
            login_password.text.toString()
        ).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
//                Send Verification email to user.
                val user = auth.currentUser
                user!!.sendEmailVerification()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(tag, "Email sent.")
                        }
                    }

                // Sign in success, update UI with the signed-in user's information
                Log.d(tag, "singInWithEmail:success")
                updateUI(user)
            } else {
                // If sign in fails, display a message to the user.
                Log.d(tag, "singInWithEmail:failure", task.exception)
                Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                updateUI(null)
            }
        }

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            if (currentUser.isEmailVerified) {
                startActivity(Intent(this, Dashboard::class.java))
                finish()
            } else {
                Toast.makeText(
                    baseContext, "Please verify your email address",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
    }


}

