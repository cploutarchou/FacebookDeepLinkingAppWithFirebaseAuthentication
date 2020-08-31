package com.cploutarchou.facebookdeeplinking

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginManager


class AuthenticatedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.authenticated_activity)

        val btnLogout = findViewById<Button>(R.id.btnLogout)

        btnLogout.setOnClickListener {
            // Logout
            if (AccessToken.getCurrentAccessToken() != null) {
                GraphRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/me/permissions/",
                    null,
                    HttpMethod.DELETE
                ) {
                    AccessToken.setCurrentAccessToken(null)
                    LoginManager.getInstance().logOut()

                    finish()
                }.executeAsync()
            }
        }

    }
}