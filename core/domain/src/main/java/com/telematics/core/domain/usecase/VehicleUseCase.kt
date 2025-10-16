package com.telematics.core.domain.usecase

import com.telematics.core.data.repository.CarServiceRepository
import com.telematics.core.model.carservice.ManufacturerData
import com.telematics.core.model.carservice.ModelData
import com.telematics.core.model.carservice.Vehicle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class VehicleUseCase @Inject constructor(
    private val carServiceRepository: CarServiceRepository
) {

    /*account vehicle*/
    fun getVehicles(): Flow<List<Vehicle>> {

        return flow {
            val data = carServiceRepository.getVehicles()
            emit(data)
        }
    }

    fun updateVehicle(vehicle: Vehicle): Flow<Unit> {

        return flow {
            val data = carServiceRepository.updateVehicle(vehicle)
            emit(data)
        }
    }

    fun createVehicle(vehicle: Vehicle): Flow<Unit> {

        return flow {
            val data = carServiceRepository.createVehicle(vehicle)
            emit(data)
        }
    }

    fun deleteVehicle(vehicle: Vehicle): Flow<Unit> {

        return flow {
            val data = carServiceRepository.deleteVehicle(vehicle.token!!)
            emit(data)
        }
    }

    fun getManufacturers(): Flow<List<ManufacturerData>> {

        return flow {
            val data = carServiceRepository.getManufacturers()
            emit(data)
        }
    }

    fun getModels(manufacturerId: Int): Flow<List<ModelData>> {

        return flow {
            val data = carServiceRepository.getModels(manufacturerId)
            emit(data)
        }
    }
}