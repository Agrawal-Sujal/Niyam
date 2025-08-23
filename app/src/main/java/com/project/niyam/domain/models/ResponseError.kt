package com.project.niyam.domain.models

import androidx.compose.runtime.Stable
import retrofit2.Response

/** These exceptions need to be handled in view-model, shown via UI and not just thrown.
 * @param genericToast Generic message for the error.
 * @param actualResponse Actual response given by the server.*/
@Stable
enum class ResponseError(val genericToast: String, var actualResponse: String? = null) {

    DOES_NOT_EXIST(genericToast = "Error! Object not found."),

    BAD_REQUEST(genericToast = "Illegal action."),

    /** Also means the user is not logged in.*/
    AUTH_HEADER_NOT_FOUND(genericToast = "Please login in order to perform this operation."), // "You need to provide an Authorization header.

    RATE_LIMIT_EXCEEDED(genericToast = "Rate limit exceeded."),

    UNAUTHORISED(genericToast = "You are not authorised to access the requested content."),

    INTERNAL_SERVER_ERROR(genericToast = "Internal server error."),

    BAD_GATEWAY(genericToast = "Error! Bad gateway."),

    SERVICE_UNAVAILABLE(genericToast = "Server outage detected. Please try again later."),

    UNKNOWN(genericToast = "Some error has occurred."),
    ;

    companion object {

        fun <T> getError(response: Response<T>): ResponseError {
            val code = response.code()
            return when (code) {
                400 -> BAD_REQUEST
                401 -> AUTH_HEADER_NOT_FOUND
                403 -> UNAUTHORISED
                404 -> DOES_NOT_EXIST
                429 -> RATE_LIMIT_EXCEEDED
                500 -> INTERNAL_SERVER_ERROR
                502 -> BAD_GATEWAY
                503 -> SERVICE_UNAVAILABLE
                else -> UNKNOWN
            }
        }
    }
}
