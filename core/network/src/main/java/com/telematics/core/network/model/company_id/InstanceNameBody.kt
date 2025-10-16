package com.telematics.core.network.model.company_id

import com.google.gson.annotations.SerializedName

data class InstanceNameBody(
    @SerializedName("InstanceName")
    val instanceName: String?
)