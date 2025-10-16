package com.telematics.core.network.api

import com.telematics.core.network.model.carservice.CarUpdateBody
import com.telematics.core.network.model.carservice.ManufacturerRest
import com.telematics.core.network.model.carservice.ModelRest
import com.telematics.core.network.model.carservice.VehicleDevicesResponse
import com.telematics.core.network.model.carservice.VehicleResponse
import com.telematics.core.network.model.rest.ApiResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CarServiceApi {
    @GET("$PATH_VERSION/$PATH_VEHICLES")
    suspend fun getVehicles(@Header("DeviceToken") deviceToken: String): ApiResponse<VehicleResponse>

    @GET("$PATH_VERSION/$PATH_VEHICLES/{vehicle_token}/$PATH_DEVICES")
    suspend fun getVehicleDevices(
        @Header("DeviceToken") deviceToken: String,
        @Path("vehicle_token") vehicleToken: String
    ): ApiResponse<VehicleDevicesResponse>

    @POST("$PATH_VERSION/$PATH_VEHICLES")
    suspend fun addVehicle(
        @Header("DeviceToken") deviceToken: String,
        @Body body: CarUpdateBody
    ): ApiResponse<String?>

    @PUT("$PATH_VERSION/$PATH_VEHICLES/{vehicle_token}/")
    suspend fun updateVehicle(
        @Header("DeviceToken") deviceToken: String,
        @Body body: CarUpdateBody,
        @Path("vehicle_token") vehicleToken: String
    ): ApiResponse<Any?>

    @DELETE("$PATH_VERSION/$PATH_VEHICLES/{vehicle_token}/")
    suspend fun deleteVehicle(
        @Header("DeviceToken") deviceToken: String,
        @Path("vehicle_token") vehicleToken: String
    ): ApiResponse<Any?>

    @GET("$PATH_VERSION/Directories/Manufacturers")
    suspend fun getManufacturers(): ApiResponse<List<ManufacturerRest>>

    @GET("$PATH_VERSION/Directories/Models")
    suspend fun getModels(@Query("manufacturerId") manufacturerId: Int): ApiResponse<List<ModelRest>>

    companion object {
        private const val PATH_VERSION = "v1"
        private const val PATH_VEHICLES = "Vehicles"
        private const val PATH_DEVICES = "devices"
    }
}