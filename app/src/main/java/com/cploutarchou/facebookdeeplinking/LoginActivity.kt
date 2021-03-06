@file:Suppress("NAME_SHADOWING")

package com.cploutarchou.facebookdeeplinking

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.View.INVISIBLE
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val tag = "MyMessage"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val progressBar = this.findViewById(R.id.progress_Bar) as ProgressBar
        val txtView: TextView = this.findViewById(R.id.progress_counter)
        progressBar.visibility = INVISIBLE
        txtView.visibility = INVISIBLE
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
        btn_reset_password.setOnClickListener {
            resetPassword()
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
        loadProgressBar()

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

    private fun loadProgressBar() {
        val progressBar = this.findViewById(R.id.progress_Bar) as ProgressBar
        val txtView: TextView = this.findViewById(R.id.progress_counter)
        var i: Int
        val handler = Handler()


        progressBar.visibility = View.VISIBLE
        txtView.visibility = View.VISIBLE
        i = progressBar.progress
        Thread {
            while (i < 100) {
                i += 5
                // Update the progress bar and display the current value
                handler.post {
                    progressBar.progress = i
                    txtView.text =
                        ("""""" + i + """/""" + progressBar.max + """""").trimIndent()
                }
                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

            }
        }.start()

    }

    private fun resetPassword() {
        if (login_username.text.toString().isEmpty()) {
            login_username.error = "Please enter your email address"
            login_username.requestFocus()
            return
        }
        auth.sendPasswordResetEmail(login_username.text.toString()).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(tag, "Email send")
                Toast.makeText(
                    baseContext, "Email successfully send.Please check your inbox",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    baseContext, "Unable to reset password. Please try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}





