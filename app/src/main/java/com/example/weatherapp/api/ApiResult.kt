package com.example.weatherapp.api

enum class ApiStatus {
    SUCCESS,
    INTERNET_NOT_WORKING,
    INTERNET_TURNED_OFF,
    HTTP_CODE_ERROR, // non 2xx error
    ERROR
}

data class ApiResult<T>(
    val status : ApiStatus,
    val data:T? = null,
    val message:String?=null
)