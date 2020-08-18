package com.cploutarchou.facebookdeeplinking

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_dashboard.*

class Dashboard : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        auth = FirebaseAuth.getInstance()
        setUserData()
        btn_change_password_submit.setOnClickListener {
            changePassword()
        }
        btn_logout.setOnClickListener {
            logout()
        }
    }

    private fun setUserData() {
        val user = auth.currentUser
        user?.let {
            val email = user.email

            setContentView(R.layout.activity_dashboard)

            val textView: TextView = findViewById(R.id.tv_logged_user_name)
            textView.text = email.toString()

            val welcome: String = getString(R.string.tv_welcome)
            tv_welcome.text = welcome
        }

    }

    private fun changePassword() {
        if (et_original_password.text.isNotEmpty() && et_new_password.text.isNotEmpty() && et_new_password_confirm.text.isNotEmpty()) {
            if (et_new_password.text.toString() == et_new_password_confirm.text.toString()) {
                val user = auth.currentUser
                if (user != null && user.email != null) {

                    val credential = EmailAuthProvider.getCredential(
                        user.email.toString(),
                        et_original_password.text.toString()
                    )

                    user.reauthenticate(credential).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(
                                this,
                                "Your account successfully reauthorized",
                                Toast.LENGTH_LONG
                            ).show()
                            user.updatePassword(et_new_password_confirm.text.toString())
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            this,
                                            "Your Password successfully updated.",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        auth.signOut()
                                        finish()
                                    }
                                }
                        } else {
                            Toast.makeText(
                                this,
                                "Unable to Reauthorize your account.Please Try again",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                }
            } else {
                Toast.makeText(this, "Password combinations not matching", Toast.LENGTH_SHORT)
                    .show()
            }

        } else {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun logout() {
        auth.signOut()
        setContentView(R.layout.activity_main)
    }
}


