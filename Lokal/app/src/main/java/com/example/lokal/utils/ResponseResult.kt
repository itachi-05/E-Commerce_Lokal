package com.example.lokal.utils

sealed class ResponseResult<T>(
    val data: T? = null, val message: String? = null, val errorBody: String? = null
) {
    class Success<T>(data: T) : ResponseResult<T>(data)
    class Error<T>(message: String?, data: T? = null, errorBody: String? = null) :
        ResponseResult<T>(data, message, errorBody)

    class Loading<T> : ResponseResult<T>()
}