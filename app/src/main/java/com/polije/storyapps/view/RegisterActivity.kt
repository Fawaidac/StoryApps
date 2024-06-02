package com.polije.storyapps.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.polije.storyapps.R
import com.polije.storyapps.customview.AppButton
import com.polije.storyapps.customview.AppEditText
import com.polije.storyapps.network.Config
import com.polije.storyapps.model.RegisterModel
import com.polije.storyapps.model.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var nameEditText: AppEditText
    private lateinit var emailEditText: AppEditText
    private lateinit var passwordEditText: AppEditText
    private lateinit var registerButton: AppButton
    private lateinit var loginTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        nameEditText = findViewById(R.id.ed_register_name)
        emailEditText = findViewById(R.id.ed_register_email)
        passwordEditText = findViewById(R.id.ed_register_password)
        registerButton = findViewById(R.id.btn_register)
        loginTextView = findViewById(R.id.tv_register_login)

        registerButton.setOnClickListener(this)
        loginTextView.setOnClickListener(this)
        setMyButtonEnable()

        nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {}
        })
        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {}
        })

        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun setMyButtonEnable() {
        val nameResult = nameEditText.text.toString().isNotEmpty()
        val emailResult = emailEditText.text.toString().isNotEmpty()
        val passwordResult = passwordEditText.text.toString().isNotEmpty()

        registerButton.isEnabled = nameResult && emailResult && passwordResult
    }

    override fun onClick(v: View?) {
        when (v) {
            registerButton -> {
                val name = nameEditText.text.toString().trim()
                val email = emailEditText.text.toString().trim()
                val password = passwordEditText.text.toString().trim()

                        val registerModel = RegisterModel(name, email, password)
                        registerUser(registerModel)


            }
            loginTextView -> {
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
            }
        }
    }

    private fun registerUser(registerModel: RegisterModel) {
        val service = Config.getServices()
        val call = service.register(registerModel)

        call.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    if (registerResponse != null && !registerResponse.error) {
                        Toast.makeText(this@RegisterActivity, "Registration successful", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@RegisterActivity, "Registration failed: ${registerResponse?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@RegisterActivity, "Registration failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Registration failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun isValidEmail(target: CharSequence): Boolean {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
    }
}