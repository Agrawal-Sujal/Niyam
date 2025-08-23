package com.project.niyam.utils

import com.google.common.net.HttpHeaders.AUTHORIZATION
import com.project.niyam.data.local.appPref.AppPref
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import kotlin.text.isNullOrEmpty

class HeaderInterceptor(
    private val appPreferences: AppPref,
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()

        return runBlocking {
            if (request.headers[AUTHORIZATION].isNullOrEmpty()) {
                runCatching {
                    withTimeoutOrNull(3000) {
                        val accessToken: String = appPreferences.token.first() ?: ""
                        if (accessToken.isNotEmpty()) {
                            request = request.newBuilder()
                                .addHeader(AUTHORIZATION, "Bearer $accessToken")
                                .build()
                        }
                    }
                }.getOrElse { it.printStackTrace() }
            }
            chain.proceed(request)
        }
    }
}
