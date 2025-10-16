package com.telematics.core.network.model.request


import com.google.gson.annotations.SerializedName

data class LogbookRequest(
    @SerializedName("DateFormat")
    val dateFormat: String?,
    @SerializedName("EndDate")
    val endDate: String?,
    @SerializedName("ReportFormat")
    val reportFormat: String?,
    @SerializedName("StartDate")
    val startDate: String?,
    @SerializedName("ToEmail")
    val toEmail: String?,
    @SerializedName("TypeOfReport")
    val typeOfReport: String?,
    @SerializedName("Units")
    val units: String?
)