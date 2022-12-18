package com.example.submission1intermediate.data.local.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class RequestRegister(
	var name: String,
	var email: String,
	var password: String
)

data class RequestLogin(
	var email: String,
	var password: String
)



@Parcelize
data class UserModel(
	var id: String,
	var name: String? = null,
	var description: String? = null,
	var photoUrl: String? = null,
	var createdAt: String? = null,
	var lat: String? = null,
	var lon: String? = null
) : Parcelable