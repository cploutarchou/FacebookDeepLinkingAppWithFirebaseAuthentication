package com.cploutarchou.facebookdeeplinking

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        auth = FirebaseAuth.getInstance()

        btn_sign_up.setOnClickListener {
            signUpUser()
        }

    }

    private fun signUpUser() {
        if (login_username.text.toString().isEmpty()) {
            login_username.error = "Please enter email"
            login_username.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(login_username.text.toString()).matches()) {
            login_username.error = "Please enter valid email"
            login_username.requestFocus()
            return
        }

        if (login_password.text.toString().isEmpty()) {
            login_password.error = "Please enter password"
            login_password.requestFocus()
            return
        }

        auth.createUserWithEmailAndPassword(
            login_username.text.toString(),
            login_password.text.toString()
        )
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        baseContext, "Sign Up failed. Try again after some time.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}