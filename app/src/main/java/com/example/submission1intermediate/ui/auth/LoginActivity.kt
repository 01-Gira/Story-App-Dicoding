package com.example.submission1intermediate.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.example.submission1intermediate.R
import com.example.submission1intermediate.data.local.preferences.UserPreference
import com.example.submission1intermediate.data.utils.Result
import com.example.submission1intermediate.databinding.ActivityLoginBinding
import com.example.submission1intermediate.ui.ViewModelFactory
import com.example.submission1intermediate.ui.auth.viewmodel.LoginViewModel
import com.example.submission1intermediate.ui.home.MainActivity
import com.example.submission1intermediate.ui.home.viewmodel.AuthViewModel
import java.util.regex.Matcher
import java.util.regex.Pattern

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory(UserPreference.getInstance(dataStore), this)
    }
    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory(UserPreference.getInstance(dataStore),this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        playAnimation()
        supportActionBar?.hide()

        showLoading(false)

        setupAuthViewModel()
        setupLoginAction()

        binding.btnRegist.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            Animatoo.animateSlideLeft(this)
        }


    }

    private fun playAnimation() {
        val imglogin = ObjectAnimator.ofFloat(binding.ivLogin, View.ALPHA, 1f).setDuration(500)
        val tvlogin = ObjectAnimator.ofFloat(binding.tvWelcome, View.ALPHA, 1f).setDuration(500)
        val tvemail = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(500)
        val tvpasssword = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(500)
        val etemail = ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(500)
        val etpassword = ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(500)

        val togetheremail = AnimatorSet().apply {
            playTogether(tvemail, etemail)
        }

        val togetherpassword = AnimatorSet().apply {
            playTogether(tvpasssword, etpassword)
        }

        val btnlogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val btnregist = ObjectAnimator.ofFloat(binding.btnRegist, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(imglogin, tvlogin, togetheremail, togetherpassword, btnlogin, btnregist)
            start()
        }
    }

    private fun setupAuthViewModel() {
        authViewModel.isLogin().observe(this) {
            if (it) {
                Toast.makeText(this@LoginActivity, getString(R.string.success_login), Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun setupLoginAction() {
        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            when{
                email.isEmpty() -> {
                    binding.edLoginEmail.error = getString(R.string.enter_email)
                }
                password.isEmpty() -> {
                    binding.edLoginPassword.error = getString(R.string.enter_password)
                }
                password.length < 6 -> {
                    binding.edLoginPassword.error = getString(R.string.length_password)
                }
                !isEmailValid(email) -> {
                    binding.edLoginEmail.error = getString(R.string.invalid_email)
                }
                else -> {
                    loginViewModel.getResponselogin(email, password).observe(this){
                        when (it) {
                            is Result.Success -> {
                                showLoading(false)
                                AlertDialog.Builder(this@LoginActivity).apply {
                                    setTitle(getString(R.string.success))
                                    setMessage(getString(R.string.success_login))
                                    setPositiveButton(getString(R.string.cont)) { _, _ ->
                                        val intent = Intent(context, MainActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                        startActivity(intent)
                                        finish()
                                    }
                                    create()
                                    show()
                                }
                                authViewModel.loginState()
                                authViewModel.saveToken(
                                    it.data.token,
                                    it.data.name
                                )
                            }
                            is Result.Loading -> {
                                showLoading(true)
                            }
                            is Result.Error -> {
                                showLoading(false)
                                AlertDialog.Builder(this@LoginActivity).apply {
                                    setTitle(getString(R.string.fail))
                                    setMessage(getString(R.string.fail_login))
                                    setPositiveButton(getString(R.string.cont)) { _, _ ->
                                        show().dismiss()
                                    }
                                    create()
                                    show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun isEmailValid(email: String): Boolean {
        val expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(email)
        return matcher.matches()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbLogin.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object{
        var token:String = "token"
    }

}