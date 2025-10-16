package com.telematics.core.data.repository

import com.telematics.core.model.carservice.ManufacturerData
import com.telematics.core.model.carservice.ModelData
import com.telematics.core.model.carservice.Vehicle
import com.telematics.core.network.mappers.toCarUpdateBody
import com.telematics.core.network.mappers.toManufacturerData
import com.telematics.core.network.mappers.toModelData
import com.telematics.core.network.mappers.toVehicle
import javax.inject.Inject

class CarServiceRepositoryImpl @Inject constructor(
    private val carServiceApi: com.telematics.core.network.api.CarServiceApi,
    private val userAuthRepository: UserAuthRepository
) : CarServiceRepository {

    override suspend fun getVehicles(): List<Vehicle> {

        val deviceToken = userAuthRepository.getDeviceToken()
        val data = carServiceApi.getVehicles(deviceToken).result
        val listVehicle = data?.cars?.map { carRest ->
            val vehicle = carRest.toVehicle()
            val isActivated = !carServiceApi.getVehicleDevices(deviceToken, carRest.token!!)
                .result?.elms.isNullOrEmpty()
            vehicle.activated = isActivated
            return@map vehicle
        }
        return listVehicle ?: emptyList()
    }

    override suspend fun createVehicle(vehicle: Vehicle) {

        val deviceToken = userAuthRepository.getDeviceToken()
        val carUpdateBody = vehicle.toCarUpdateBody()
        carServiceApi.addVehicle(deviceToken, carUpdateBody)
    }

    override suspend fun updateVehicle(vehicle: Vehicle) {

        val deviceToken = userAuthRepository.getDeviceToken()
        val carUpdateBody = vehicle.toCarUpdateBody()
        carServiceApi.updateVehicle(deviceToken, carUpdateBody, vehicle.token!!)
    }

    override suspend fun deleteVehicle(vehicleToken: String) {

        val deviceToken = userAuthRepository.getDeviceToken()
        carServiceApi.deleteVehicle(deviceToken, vehicleToken)
    }

    override suspend fun getManufacturers(): List<ManufacturerData> {

        val data = carServiceApi.getManufacturers().result
        return data?.map { it.toManufacturerData() } ?: emptyList()
    }

    override suspend fun getModels(manufacturerId: Int): List<ModelData> {

        val data = carServiceApi.getModels(manufacturerId).result
        return data?.map { it.toModelData() } ?: emptyList()
    }
}

interface CarServiceRepository {
    suspend fun getVehicles(): List<Vehicle>
    suspend fun createVehicle(vehicle: Vehicle)
    suspend fun updateVehicle(vehicle: Vehicle)
    suspend fun deleteVehicle(vehicleToken: String)
    suspend fun getManufacturers(): List<ManufacturerData>
    suspend fun getModels(manufacturerId: Int): List<ModelData>
}