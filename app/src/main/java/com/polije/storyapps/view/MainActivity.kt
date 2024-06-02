package com.polije.storyapps.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.polije.storyapps.R
import com.polije.storyapps.customview.AppButton
import com.polije.storyapps.customview.AppEditText
import com.polije.storyapps.network.Config
import com.polije.storyapps.model.LoginModel
import com.polije.storyapps.model.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var emailEditText: AppEditText
    private lateinit var passwordEditText: AppEditText
    private lateinit var loginButton: AppButton
    private lateinit var registerTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var passTextView: TextView
    private lateinit var welllcomeTextView: TextView
    private lateinit var linearLayout: LinearLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        emailEditText = findViewById(R.id.ed_login_email)
        passwordEditText = findViewById(R.id.ed_login_password)
        loginButton = findViewById(R.id.btn_login)
        registerTextView = findViewById(R.id.tv_login_register)
        linearLayout = findViewById(R.id.layout)
        welllcomeTextView = findViewById(R.id.tv_wellcome)
        emailTextView = findViewById(R.id.tv_email)
        passTextView = findViewById(R.id.tv_pass)

        loginButton.setOnClickListener(this)
        registerTextView.setOnClickListener(this)
        setMyButtonEnable()
        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {}
        })
        playAnimation()
        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {}
        })
    }
    private fun playAnimation() {
        val fadeInLoginButton = ObjectAnimator.ofFloat(loginButton, View.ALPHA, 1f).setDuration(300)
        val fadeInSignupButton = ObjectAnimator.ofFloat(linearLayout, View.ALPHA,  1f).setDuration(300)
        val fadeInWellcomeTextView = ObjectAnimator.ofFloat(welllcomeTextView, View.ALPHA,  1f).setDuration(300)
        val fadeInEmailTextView = ObjectAnimator.ofFloat(emailEditText, View.ALPHA,  1f).setDuration(300)
        val fadeInPassTextView = ObjectAnimator.ofFloat(passwordEditText, View.ALPHA,  1f).setDuration(300)
        val email = ObjectAnimator.ofFloat(emailTextView, View.ALPHA,  1f).setDuration(300)
        val pass = ObjectAnimator.ofFloat(passTextView, View.ALPHA,  1f).setDuration(300)
        fadeInWellcomeTextView.start()
        email.start()
        fadeInEmailTextView.start()
        pass.start()
        fadeInPassTextView.start()
        fadeInLoginButton.start()
        fadeInSignupButton.start()

        val together = AnimatorSet().apply {
            playTogether(email, fadeInEmailTextView)
        }
        val together2 = AnimatorSet().apply {
            playTogether(pass, fadeInPassTextView)
        }

        AnimatorSet().apply {
            playSequentially(fadeInWellcomeTextView, together, together2, fadeInLoginButton, fadeInSignupButton)
            start()
        }
    }



    private fun saveSessionData(token: String) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", true)
        editor.putString("token", token)
        editor.apply()
    }
    private fun setMyButtonEnable() {
        val emailResult = emailEditText.text.toString().isNotEmpty()
        val passwordResult = passwordEditText.text.toString().isNotEmpty()

        loginButton.isEnabled =  emailResult && passwordResult
    }
    override fun onClick(v: View?) {
        when (v) {
            loginButton -> {
                val email = emailEditText.text.toString().trim()
                val password = passwordEditText.text.toString().trim()
                        val loginModel = LoginModel(email, password)
                        loginUser(loginModel)
            }
            registerTextView -> {
                val i = Intent(this, RegisterActivity::class.java)
                startActivity(i)
            }
        }
    }

    private fun loginUser(loginModel: LoginModel) {
        val service = Config.getServices()
        val call = service.login(loginModel)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null && !loginResponse.error) {
                        val token = loginResponse.loginResult?.token ?: ""
                        saveSessionData(token)
                        Log.e("Token", token.toString())
                        val intent = Intent(this@MainActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@MainActivity, "Login failed: ${loginResponse?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Login failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}