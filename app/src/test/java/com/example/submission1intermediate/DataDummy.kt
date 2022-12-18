package com.example.submission1intermediate

import com.example.submission1intermediate.data.local.model.RequestLogin
import com.example.submission1intermediate.data.local.model.RequestRegister
import com.example.submission1intermediate.data.local.model.UserModel
import com.example.submission1intermediate.data.remote.response.*

object DataDummy {

    fun generateDummyUserModel(): UserModel {
        return UserModel(
            id = "String",
            name = "string",
            description = "string",
            photoUrl = "string",
            createdAt = "string",
        )
    }

    fun generateDummyStoryEntity(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "this name $i",
                "this is description $i",
                "this is photoUrl $i",
                "this is createdAt $i",
                0.2,
                0.1
            )
            items.add(story)
        }
        return items
    }

    fun generateDummyStoriesResponse(): StoriesResponse {
        return StoriesResponse(false, "Success", generateDummyStoryEntity())
    }

    fun generateDummyLoginResult(): LoginResult{
        return LoginResult("123@gmail.com", "123456", "token")
    }

    fun generateDummyLoginResponse(): LoginResponse {
        return LoginResponse(false, "Login successfully", loginResult = generateDummyLoginResult())
    }

    fun generateDummyRequestRegister(): RequestRegister {
        return RequestRegister("gira", "123@gmail.com", "1234567")
    }

    fun generateDummyRegisterResponse(): RegisterResponse {
        return RegisterResponse(
            false,
            "success"
        )
    }

    fun generateDummyFileUploadResponse(): FileUploadResponse {
        return FileUploadResponse(
            false,
            "success"
        )
    }

}