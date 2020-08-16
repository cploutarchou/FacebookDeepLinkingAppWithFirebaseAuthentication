package com.cploutarchou.facebookdeeplinking

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

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

        btn_log_in.setOnClickListener {
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

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(currentUser: FirebaseUser?): FirebaseUser? {
        return currentUser
    }


}

