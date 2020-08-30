package com.cploutarchou.facebookdeeplinking

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cploutarchou.facebookdeeplinking.R.layout.activity_main
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.activity_main.*


@Suppress("DEPRECATION")
class Dashboard : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var callbackManager: CallbackManager? = null
    private var facebookLoginBtn: LoginButton? = findViewById(R.id.btn_facebook_login)


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        callbackManager = CallbackManager.Factory.create()

        setContentView(R.layout.activity_dashboard)
        auth = FirebaseAuth.getInstance()
        setUserData()
        btn_change_password_submit.setOnClickListener {
            changePassword()
        }
        btn_logout.setOnClickListener {
            logout()
        }

        btn_facebook_login.setOnClickListener {
            facebookLogin()
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


    private fun facebookLogin() {

        facebookLoginBtn?.setReadPermissions(listOf("public_profile", "email"))
        // If you are using in a fragment, call loginButton.setFragment(this)

        // Callback registration
        facebookLoginBtn?.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    Log.d("TAG", "Success Login")
                    getUserProfile(loginResult?.accessToken, loginResult?.accessToken?.userId)
                }

                override fun onCancel() {
                    Toast.makeText(this@Dashboard, "Login Cancelled", Toast.LENGTH_LONG).show()
                }

                override fun onError(exception: FacebookException) {
                    Toast.makeText(this@Dashboard, exception.message, Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun facebookLogout() {
        LoginManager.getInstance().logOut()
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
        facebookLogout()
        auth.signOut()
        finish()
        setContentView(activity_main)
    }

    @SuppressLint("LongLogTag")
    fun getUserProfile(token: AccessToken?, userId: String?) {

        val parameters = Bundle()
        parameters.putString(
            "fields",
            "id, first_name, middle_name, last_name, name, picture, email"
        )
        GraphRequest(token,
            "/$userId/",
            parameters,
            HttpMethod.GET,
            GraphRequest.Callback { response ->
                val jsonObject = response.jsonObject

                // Facebook Access Token
                // You can see Access Token only in Debug mode.
                // You can't see it in Logcat using Log.d, Facebook did that to avoid leaking user's access token.
                if (BuildConfig.DEBUG) {
                    FacebookSdk.setIsDebugEnabled(true)
                    FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS)
                }

                // Facebook Id
                if (jsonObject.has("id")) {
                    val facebookId = jsonObject.getString("id")
                    Log.i("Facebook Id: ", facebookId.toString())
                } else {
                    Log.i("Facebook Id: ", "Not exists")
                }


                // Facebook First Name
                if (jsonObject.has("first_name")) {
                    val facebookFirstName = jsonObject.getString("first_name")
                    Log.i("Facebook First Name: ", facebookFirstName)
                } else {
                    Log.i("Facebook First Name: ", "Not exists")
                }


                // Facebook Middle Name
                if (jsonObject.has("middle_name")) {
                    val facebookMiddleName = jsonObject.getString("middle_name")
                    Log.i("Facebook Middle Name: ", facebookMiddleName)
                } else {
                    Log.i("Facebook Middle Name: ", "Not exists")
                }


                // Facebook Last Name
                if (jsonObject.has("last_name")) {
                    val facebookLastName = jsonObject.getString("last_name")
                    Log.i("Facebook Last Name: ", facebookLastName)
                } else {
                    Log.i("Facebook Last Name: ", "Not exists")
                }


                // Facebook Name
                if (jsonObject.has("name")) {
                    val facebookName = jsonObject.getString("name")
                    Log.i("Facebook Name: ", facebookName)
                } else {
                    Log.i("Facebook Name: ", "Not exists")
                }


                // Facebook Profile Pic URL
                if (jsonObject.has("picture")) {
                    val facebookPictureObject = jsonObject.getJSONObject("picture")
                    if (facebookPictureObject.has("data")) {
                        val facebookDataObject = facebookPictureObject.getJSONObject("data")
                        if (facebookDataObject.has("url")) {
                            val facebookProfilePicURL = facebookDataObject.getString("url")
                            Log.i("Facebook Profile Pic URL: ", facebookProfilePicURL)
                        }
                    }
                } else {
                    Log.i("Facebook Profile Pic URL: ", "Not exists")
                }

                // Facebook Email
                if (jsonObject.has("email")) {
                    val facebookEmail = jsonObject.getString("email")
                    Log.i("Facebook Email: ", facebookEmail)
                } else {
                    Log.i("Facebook Email: ", "Not exists")
                }
            }).executeAsync()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

}
