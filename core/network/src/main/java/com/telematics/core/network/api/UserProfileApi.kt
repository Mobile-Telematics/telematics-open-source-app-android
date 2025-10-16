package com.telematics.core.network.api

import com.telematics.core.network.model.request.UpdateUserProfileRequest
import com.telematics.core.network.model.response.GetUserProfileResponse
import com.telematics.core.network.model.response.SearchRecordResponse
import com.telematics.core.network.model.response.UploadUserImageResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface UserProfileApi {

    @GET("mobileapp/v1/user/{userId}/profile")
    suspend fun getUserProfile(
        @Path("userId") userId: String,
    ): Response<GetUserProfileResponse>

    @PUT("mobileapp/v1/user/{userId}/profile")
    suspend fun updateUserProfile(
        @Path("userId") userId: String,
        @Body body: UpdateUserProfileRequest
    ): Response<ResponseBody>

    @DELETE("mobileapp/v1/user/{userId}")
    suspend fun deleteUserProfile(
        @Path("userId") userId: String,
    ): Response<ResponseBody>

    @Multipart
    @POST("mobileapp/v1/user/upload-photo")
    suspend fun uploadUserImage(
        @Part("uid") userId: RequestBody?,
        @Part image: MultipartBody.Part
    ): Response<UploadUserImageResponse>

    @GET("mobileapp/v1/user/profile/legacy")
    @Deprecated("Deprecated, will be removed", ReplaceWith("getUserProfile()"))
    suspend fun getUserProfileLegacy(): Response<GetUserProfileResponse>
}