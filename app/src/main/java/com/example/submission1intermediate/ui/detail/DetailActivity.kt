package com.example.submission1intermediate.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.example.submission1intermediate.R
import com.example.submission1intermediate.data.local.model.UserModel
import com.example.submission1intermediate.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showLoading(true)

        val extra = intent.getParcelableExtra<UserModel>(EXTRA_STORY)
        setDataStory(
            extra?.name ?: "null",
            extra?.description ?: "null",
            extra?.photoUrl ?: "null"

        )

    }

    private fun setDataStory(name: String, description: String, photoUrl: String) {

        showLoading(false)

        supportActionBar?.title = name

        binding.apply {
            Glide.with(this@DetailActivity)
                .load(photoUrl)
                .into(ivPhoto)
            tvDetailName.text = name
            tvDetailDesc.text = description
        }

    }


    private fun showLoading(isLoading: Boolean) {
        binding.pbDetail.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_STORY = "extra_story"
    }
}