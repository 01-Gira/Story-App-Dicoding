package com.example.submission1intermediate.ui.addstory


import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.submission1intermediate.R
import com.example.submission1intermediate.data.local.preferences.UserPreference
import com.example.submission1intermediate.data.utils.Result
import com.example.submission1intermediate.databinding.ActivityAddStoryBinding
import com.example.submission1intermediate.ui.ViewModelFactory
import com.example.submission1intermediate.ui.addstory.utils.rotateBitmap
import com.example.submission1intermediate.ui.addstory.utils.uriToFile
import com.example.submission1intermediate.ui.addstory.viewmodel.AddStoryViewModel
import com.example.submission1intermediate.ui.home.MainActivity
import com.example.submission1intermediate.ui.home.viewmodel.AuthViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding
    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory(UserPreference.getInstance(dataStore), this)
    }
    private val addStoryViewModel: AddStoryViewModel by viewModels {
        ViewModelFactory(UserPreference.getInstance(dataStore), this)
    }

    private var getFile: File? = null
    private lateinit var token: String


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.no_permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_story)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.new_story)
        showLoading(false)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        setupAuthViewModel()


        binding.btnCamerax.setOnClickListener { startCameraX() }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnUpload.setOnClickListener { uploadImage() }
    }


    private fun setupAuthViewModel() {
        showLoading(false)
        authViewModel.getToken().observe(this) {
            token = it
        }
    }

    private fun uploadImage() {
        showLoading(true)
        val desc = binding.etDescription.text.toString()
        if (getFile != null) {
            when {
                desc.isEmpty() -> {
                    binding.etDescription.error = getString(R.string.empty_desc)
                }
            }
            val file = reduceFileImage(getFile as File)
            val description =
                binding.etDescription.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            addStoryViewModel.upload(token, imageMultipart, description).observe(this){
                when (it){
                    is Result.Success -> {
                        showLoading(false)
                        AlertDialog.Builder(this@AddStoryActivity).apply {
                            setTitle(getString(R.string.success))
                            setMessage(getString(R.string.success_upload))
                            setPositiveButton(getString(R.string.cont)){_, _ ->
                                val intent = Intent(context, MainActivity::class.java)
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
                    is Result.Error ->{
                        showLoading(false)
                        AlertDialog.Builder(this@AddStoryActivity).apply {
                            setTitle(getString(R.string.fail))
                            setMessage(getString(R.string.fail_internet) + ", ${it.error}")
                            setPositiveButton(getString(R.string.cont)){_, _ ->
                                show().dismiss()
                            }
                        }

                    }
                }
            }


        }

        else {
            showLoading(false)
            AlertDialog.Builder(this@AddStoryActivity).apply {
                setTitle(getString(R.string.fail))
                setMessage(getString(R.string.empty_picture))
                setPositiveButton(getString(R.string.cont)) { _, _ ->
                    show().dismiss()
                }
                create()
                show()
            }
        }
    }



    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun reduceFileImage(file: File): File {
        return file
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            getFile = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(myFile.path),
                isBackCamera
            )

            binding.previewImage.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddStoryActivity)
            getFile = myFile
            binding.previewImage.setImageURI(selectedImg)
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbAddstory.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

}