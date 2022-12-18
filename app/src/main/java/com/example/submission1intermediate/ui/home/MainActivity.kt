package com.example.submission1intermediate.ui.home

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submission1intermediate.R
import com.example.submission1intermediate.data.local.preferences.UserPreference
import com.example.submission1intermediate.databinding.ActivityMainBinding
import com.example.submission1intermediate.ui.DarkModeViewModel
import com.example.submission1intermediate.ui.ViewModelFactory
import com.example.submission1intermediate.ui.adapter.ListStoryAdapter
import com.example.submission1intermediate.ui.adapter.LoadingStateAdapter
import com.example.submission1intermediate.ui.addstory.AddStoryActivity
import com.example.submission1intermediate.ui.auth.LoginActivity
import com.example.submission1intermediate.ui.home.viewmodel.AuthViewModel
import com.example.submission1intermediate.ui.home.viewmodel.MainViewModel
import com.example.submission1intermediate.ui.map.MapsActivity
import com.google.android.material.switchmaterial.SwitchMaterial


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory(UserPreference.getInstance(dataStore), this)
    }
    private lateinit var token: String
    private val mainViewModel: MainViewModel by viewModels{
        ViewModelFactory(UserPreference.getInstance(dataStore), this)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvListStory.layoutManager = LinearLayoutManager(this@MainActivity)

        showLoading(true)
        showNoData(false)

        setupViewModel()

        setActionBtn()
    }

    private fun getData(token: String) {
        showLoading(false)
        val adapter = ListStoryAdapter()

        binding.rvListStory.adapter = adapter
        binding.rvListStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        mainViewModel.getStory(token).observe(this){
            if (it != null){
                adapter.submitData(lifecycle, it)
            }else {
                showNoData(true)
            }

        }

    }

    private fun setActionBtn() {
        binding.apply {
            swipeRefresh.setOnRefreshListener {
                mainViewModel.getStory(token).observe(this@MainActivity){
                    getData(token)
                }
                binding.swipeRefresh.isRefreshing = false
            }

            fabAdd.setOnClickListener{
                val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun showNoData(b: Boolean) {
        showLoading(false)
        binding.tvError.visibility = if (b) View.VISIBLE else View.GONE
    }

    private fun setupViewModel() {
        authViewModel.isLogin().observe(this) {
            if (!it) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        authViewModel.getToken().observe(this) {
            token = it
            getData(token)
        }

    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        val switch = menu?.findItem(R.id.switch_theme)?.actionView?.findViewById<SwitchMaterial>(R.id.switch2)

        val darkModeViewModel = ViewModelProvider(this, ViewModelFactory(UserPreference.getInstance(dataStore), this)).get(
            DarkModeViewModel::class.java
        )

        darkModeViewModel.getThemeSettings().observe(this) {isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                switch?.isChecked = true
            }else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                switch?.isChecked = false
            }
        }

        switch?.setOnCheckedChangeListener{ _: CompoundButton, isChecked: Boolean ->
            darkModeViewModel.saveThemeSetting(isChecked)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_maps -> {
                startActivity(Intent(this@MainActivity, MapsActivity::class.java))
            }
            R.id.menu_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }

            R.id.menu_sign_out -> {
                showLoading(false)
                AlertDialog.Builder(this@MainActivity).apply {
                    setTitle(getString(R.string.sign_out))
                    setMessage(getString(R.string.confirm_signout))
                    setNegativeButton(getString(R.string.no)){_, _ ->
                        show().dismiss()
                    }
                    setPositiveButton(getString(R.string.yes)) { _, _ ->
                        authViewModel.logout()
                    }

                    create()
                    show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbMain.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

}