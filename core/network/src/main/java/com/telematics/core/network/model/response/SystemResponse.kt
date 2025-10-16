package com.telematics.core.network.model.response

import com.google.gson.annotations.SerializedName

data class SystemResponse(
    @SerializedName("accesstoken")
    val accessToken: String?,
    @SerializedName("appid")
    val appId: String?,
    @SerializedName("appname")
    val appName: String?,
    @SerializedName("companyid")
    val companyId: String?,
    @SerializedName("companyname")
    val companyName: String?,
    @SerializedName("customid")
    val customId: String?,
    @SerializedName("firebaseid")
    val firebaseId: String?,
    @SerializedName("instanceid")
    val instanceId: String?,
    @SerializedName("instancename")
    val instanceName: String?,
    @SerializedName("refreshtoken")
    val refreshToken: String?,
    @SerializedName("telematicsuserid")
    val telematicsUserId: String?,
    @SerializedName("simplemode")
    val simpleMode: Boolean?
)
