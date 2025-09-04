package com.project.niyam.di

import com.project.niyam.data.local.appPref.AppPref
import com.project.niyam.services.remote.AuthServices
import com.project.niyam.services.remote.FriendServices
import com.project.niyam.services.remote.TasksServices
import com.project.niyam.utils.Constants
import com.project.niyam.utils.HeaderInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
class ServiceModule {

    private val okHttpClient by lazy {
        OkHttpClient
            .Builder()
            .build()
    }

    private fun constructRetrofit(appPreferences: AppPref): Retrofit =
        Retrofit.Builder()
            .client(
                okHttpClient
                    .newBuilder()
                    .addInterceptor(HeaderInterceptor(appPreferences))
                    // .addInterceptor (HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                    .build(),
            )
            .baseUrl(Constants.NIYAM_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Singleton
    @Provides
    fun provideAuthServices(appPreferences: AppPref): AuthServices =
        constructRetrofit(appPreferences)
            .create(AuthServices::class.java)

    @Singleton
    @Provides
    fun provideTasksServices(appPreferences: AppPref): TasksServices =
        constructRetrofit(appPreferences)
            .create(TasksServices::class.java)

    @Singleton
    @Provides
    fun provideFriendServices(appPreferences: AppPref): FriendServices =
        constructRetrofit(appPreferences)
            .create(FriendServices::class.java)
}
