package com.project.niyam.utils

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.project.niyam.domain.models.ApiError
import com.project.niyam.domain.models.ResponseError
import com.project.niyam.domain.models.ResponseError.Companion.getError
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.jvm.java

object Utils {

    fun <T> parseResponse(response: Response<T>): Resource<T> {
        if (response.isSuccessful) {
            Log.d("Response Body ", response.body()!!.toString())
            return Resource.success(response.body()!!)
        }
        val error = response.error()

        if (error == null) {
            return Resource.failure(error = ApiError(error = ResponseError.getError(response).genericToast))
        }
        val gson = Gson()
        val errorResponse = gson.fromJson(error, ApiError::class.java)
        return Resource.failure(error = errorResponse)
    }

    /** Get human readable error.
     *
     * **CAUTION:** If this function is called once, calling it further with the same [Response] instance will result in an empty
     * string. Store this function's result for multiple use cases.*/
    fun <T> Response<T>.error(): String? = this.errorBody()?.string()
}

@SuppressLint("DefaultLocale")
fun Int.toHms(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val seconds = this % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

fun String.toSeconds(): Int {
    val parts = split(":").map { it.toIntOrNull() ?: 0 }
    return when (parts.size) {
        3 -> parts[0] * 3600 + parts[1] * 60 + parts[2]
        2 -> parts[0] * 60 + parts[1]
        else -> 0
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun secondsUntil(endDate: LocalDate, endTime: LocalTime): Long {
    val date = endDate
    val time = endTime

    val endDateTime = LocalDateTime.of(date, time)

    // Current time in same zone
    val now = LocalDateTime.now(ZoneId.systemDefault())

    // Calculate seconds difference
    return ChronoUnit.SECONDS.between(now, endDateTime)
}
