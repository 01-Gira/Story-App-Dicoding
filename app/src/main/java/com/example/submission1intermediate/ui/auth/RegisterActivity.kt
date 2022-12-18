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
import com.example.submission1intermediate.databinding.ActivityRegisterBinding
import com.example.submission1intermediate.ui.ViewModelFactory
import com.example.submission1intermediate.ui.auth.viewmodel.RegisterViewModel
import com.example.submission1intermediate.ui.home.MainActivity
import com.example.submission1intermediate.ui.home.viewmodel.AuthViewModel
import java.util.regex.Matcher
import java.util.regex.Pattern

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory(UserPreference.getInstance(dataStore), this)
    }
    private val registerViewModel: RegisterViewModel by viewModels {
        ViewModelFactory(UserPreference.getInstance(dataStore),this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.sign_up)
        showLoading(false)

        setupAuthViewModel()
        setupRegister()
        playAnimation()

        binding.btnBack.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
            Animatoo.animateSlideRight(this)
        }
    }
    private fun setupRegister() {
        binding.btnRegist.setOnClickListener {
            showLoading(true)
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password1 = binding.edRegisterPassword1.text.toString()
            val password2 = binding.edRegisterPassword2.text.toString()
            when {
                name.isEmpty() -> {
                    showLoading(false)
                    binding.edRegisterName.error = getString(R.string.enter_name)

                }
                email.isEmpty() -> {
                    showLoading(false)
                    binding.edRegisterEmail.error = getString(R.string.enter_email)
                }
                password1.isEmpty() -> {
                    showLoading(false)
                    binding.edRegisterPassword1.error = getString(R.string.enter_password)
                }
                password2.isEmpty() -> {
                    showLoading(false)
                    binding.edRegisterPassword2.error = getString(R.string.enter_password)
                }
                password1.length < 6 -> {
                    showLoading(false)
                    binding.edRegisterPassword1.error = getString(R.string.length_password)
                }
                password2.length < 6 -> {
                    showLoading(false)
                    binding.edRegisterPassword2.error = getString(R.string.length_password)
                }
                password1 != password2 -> {
                    showLoading(false)
                    binding.edRegisterPassword1.error = getString(R.string.confirm_password)
                    binding.edRegisterPassword2.error = getString(R.string.confirm_password)
                }
                !isEmailValid(email) -> {
                    showLoading(false)
                    binding.edRegisterEmail.error = getString(R.string.invalid_email)
                }
                else -> {
                    registerViewModel.getResponseRegister(name, email, password2).observe(this){
                        when (it) {
                            is Result.Success -> {
                                showLoading(false)
                                AlertDialog.Builder(this@RegisterActivity).apply {
                                    setTitle(getString(R.string.success))
                                    setMessage(getString(R.string.success_register))
                                    setPositiveButton(getString(R.string.cont)) { _, _ ->
                                        val intent = Intent(context, LoginActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                        startActivity(intent)
                                        finish()
                                    }
                                    create()
                                    show()
                                }
                            }
                            is Result.Loading -> {
                                showLoading(true)
                            }
                            is Result.Error -> {
                                showLoading(false)
                                AlertDialog.Builder(this@RegisterActivity).apply {
                                    setTitle(getString(R.string.fail))
                                    setMessage(getString(R.string.fail_register))
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

    private fun setupAuthViewModel() {

        authViewModel.isLogin().observe(this) {
            if (it) {
                Toast.makeText(this@RegisterActivity, getString(R.string.success_login), Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun playAnimation() {
        val imglogin = ObjectAnimator.ofFloat(binding.imageView3, View.ALPHA, 1f).setDuration(500)
        val tvLogin = ObjectAnimator.ofFloat(binding.tvSignup, View.ALPHA, 1f).setDuration(500)

        val tvname = ObjectAnimator.ofFloat(binding.tvName, View.ALPHA, 1f).setDuration(500)
        val edname = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(500)

        val tvemail = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(500)
        val edemail = ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(500)

        val tvpassword = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(500)
        val edpassword = ObjectAnimator.ofFloat(binding.edRegisterPassword1, View.ALPHA, 1f).setDuration(500)

        val tvpassword2 =  ObjectAnimator.ofFloat(binding.tvConfirmPassword, View.ALPHA, 1f).setDuration(500)
        val edpassword2 = ObjectAnimator.ofFloat(binding.edRegisterPassword2, View.ALPHA, 1f).setDuration(500)

        val togethername = AnimatorSet().apply {
            playTogether(tvname, edname)
        }

        val togetheremail = AnimatorSet().apply {
            playTogether(tvemail, edemail)
        }

        val togetherpassword = AnimatorSet().apply {
            playTogether(tvpassword, edpassword)
        }

        val togetherpassword2 = AnimatorSet().apply {
            playTogether(tvpassword2, edpassword2)
        }

        val btnback= ObjectAnimator.ofFloat(binding.btnRegist, View.ALPHA, 1f).setDuration(500)
        val btnregist = ObjectAnimator.ofFloat(binding.btnBack, View.ALPHA, 1f).setDuration(500)
        AnimatorSet().apply {
            playSequentially(imglogin, tvLogin, togethername, togetheremail, togetherpassword, togetherpassword2, btnback, btnregist)
            start()
        }
    }

    private fun isEmailValid(email: String): Boolean {
        val expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
        val pattern: Pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(email)
        return matcher.matches()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbRegist.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        Animatoo.animateSlideRight(this)
    }
}