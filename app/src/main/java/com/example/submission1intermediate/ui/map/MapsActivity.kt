package com.example.submission1intermediate.ui.map

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submission1intermediate.R
import com.example.submission1intermediate.data.local.preferences.UserPreference
import com.example.submission1intermediate.data.remote.response.ListStoryItem
import com.example.submission1intermediate.data.utils.LocationConverter
import com.example.submission1intermediate.data.utils.Result
import com.example.submission1intermediate.databinding.ActivityMapsBinding
import com.example.submission1intermediate.ui.ViewModelFactory
import com.example.submission1intermediate.ui.adapter.ListStoryMapsAdapter
import com.example.submission1intermediate.ui.home.MainActivity
import com.example.submission1intermediate.ui.home.viewmodel.AuthViewModel
import com.example.submission1intermediate.ui.map.viewmodel.MapsViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mMap: GoogleMap
    private lateinit var token: String
    private var storiesWithLocation = listOf<ListStoryItem>()
    private val mapsViewModel : MapsViewModel by viewModels {
        ViewModelFactory(UserPreference.getInstance(dataStore),this)
    }
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvMaps.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvMaps.addItemDecoration(itemDecoration)

        setupAuthViewModel()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    private fun setupAuthViewModel() {
        authViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore), this)
        )[AuthViewModel::class.java]
        authViewModel.getToken().observe(this){
            token = it
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        setMapStyle()
        getMyLocation()


        mapsViewModel.getStories(token).observe(this){
            when (it){
                is Result.Success -> {
                    showLoading(false)
                    val stories = it.data
                    setDataStories(stories)

                }
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Error -> {
                    showLoading(false)
                    AlertDialog.Builder(this@MapsActivity).apply {
                        setTitle(getString(R.string.fail))
                        setMessage(getString(R.string.fail_data) + ", ${it.error}")
                        setPositiveButton(getString(R.string.cont)){_, _ ->
                            val intent = Intent(context, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun setDataStories(stories: List<ListStoryItem>) {

        val listUserAdapter = ListStoryMapsAdapter(stories)
        binding.rvMaps.adapter = listUserAdapter
        setMarker(stories)

        listUserAdapter.setOnItemClickCallback(object : ListStoryMapsAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListStoryItem) {
                val posisition = LocationConverter.toLatlng(data.lat, data.lon)
                if (posisition != null) {
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            posisition, DEFAULT_ZOOM
                        )
                    )
                } else {
                    Toast.makeText(
                        this@MapsActivity,
                        getString(R.string.no_location),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        })
    }

    private fun setMarker(it: List<ListStoryItem>) {
        if (it.isNotEmpty()) {
            for (story in it) {
                val posisition = LocationConverter.toLatlng(story.lat, story.lon)
                val address = LocationConverter.getStringAddress(posisition, this)
                if (posisition != null) {
                    storiesWithLocation = storiesWithLocation + story
                    mMap.addMarker(
                        MarkerOptions().position(posisition).title(story.name).snippet(address)
                    )

                }
            }
        }
        if (storiesWithLocation.isNotEmpty()) {
            val position =
                LocationConverter.toLatlng(storiesWithLocation[0].lat, storiesWithLocation[0].lon)!!
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    position, INITIAL_ZOOM
                )
            )
        }

    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbMaps.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object{
        const val TAG = "MapsActivity"
        const val DEFAULT_ZOOM = 15f
        const val INITIAL_ZOOM = 6f
    }
}