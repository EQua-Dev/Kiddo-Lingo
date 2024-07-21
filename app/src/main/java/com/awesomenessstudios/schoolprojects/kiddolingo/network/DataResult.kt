package com.awesomenessstudios.schoolprojects.kiddolingo.network

sealed class DataResult<out T>{
    data class Success<out T>(val value: T): DataResult<T>()
    data class Failure(
        val error: String
    ) : DataResult<Nothing>()
    object Loading : DataResult<Nothing>()

}
