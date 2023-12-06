package com.example.lokal.utils

import android.util.Log
import retrofit2.Response


class ResponseHandler {
    suspend fun <T : Any> callAPI(call: suspend () -> Response<T>): ResponseResult<T> {
        return try {
            val apiResponse = call()
            if (apiResponse.isSuccessful && apiResponse.body() != null) {
                ResponseResult.Success(apiResponse.body()!!)
            } else {
                val errorObj = apiResponse.errorBody()!!.charStream().readText()
                ResponseResult.Error(
                    "internal server error"
                )
            }
        } catch (e: Exception) {
            Log.i("checkingException", e.message.toString())
            ResponseResult.Error("something went wrong")
        }
    }
}
