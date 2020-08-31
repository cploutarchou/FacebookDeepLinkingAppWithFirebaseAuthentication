package com.cploutarchou.facebookdeeplinking


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cploutarchou.facebookdeeplinking.R.layout.activity_main
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_dashboard.*
import java.util.*


class Dashboard : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var callbackManager: CallbackManager? = null


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


        var btnLoginFacebook = findViewById<Button>(R.id.btn_facebook_login)

        btnLoginFacebook.setOnClickListener(View.OnClickListener {
            // Login
            callbackManager = CallbackManager.Factory.create()
            LoginManager.getInstance()
                .logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        Log.d("MainActivity", "Facebook token: " + loginResult.accessToken.token)
                        startActivity(Intent(applicationContext, AuthenticatedActivity::class.java))
                    }

                    override fun onCancel() {
                        Log.d("MainActivity", "Facebook onCancel.")

                    }

                    override fun onError(error: FacebookException) {
                        Log.d("MainActivity", "Facebook onError.")

                    }
                })
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        callbackManager?.onActivityResult(requestCode, resultCode, data)

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
        if (et_original_password.text.isNotEmpty() && et_new_password.text.isNotEmpty() &&
            et_new_password_confirm.text.isNotEmpty()
        ) {
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
                Toast.makeText(
                    this, "Password combinations not matching",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

        } else {
            Toast.makeText(
                this, "Please fill all required fields",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun logout() {

        auth.signOut()
        finish()
        setContentView(activity_main)
    }


}
