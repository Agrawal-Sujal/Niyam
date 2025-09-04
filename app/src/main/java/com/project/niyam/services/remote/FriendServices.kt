package com.project.niyam.services.remote

import com.project.niyam.domain.models.friends.FriendResponse
import com.project.niyam.domain.models.friends.RequestResponse
import com.project.niyam.domain.models.friends.RequestSend
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FriendServices {

    @GET("friends/")
    suspend fun getAllFriends(): Response<List<FriendResponse>>

    @POST("friends/")
    suspend fun addFriend(@Body request: RequestSend): Response<RequestResponse>


}