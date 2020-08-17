package com.cploutarchou.facebookdeeplinking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_dashboard.*

class Dashboard : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        auth = FirebaseAuth.getInstance()
        setUserData()
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
}

