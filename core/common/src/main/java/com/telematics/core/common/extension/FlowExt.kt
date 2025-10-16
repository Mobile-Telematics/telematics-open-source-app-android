package com.telematics.core.common.extension

import androidx.lifecycle.MutableLiveData
import com.telematics.core.common.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

fun <T> Flow<T>.setLiveData(liveData: MutableLiveData<Resource<T>>): Flow<T> {
    return this
        .onStart {
            liveData.postValue(Resource.Loading())
        }
        .onEach {
            liveData.postValue(Resource.Success(it))
        }
        .catch {
            liveData.postValue(Resource.Failure(it))
        }
}

fun <T> Flow<T>.setLiveDataForResult(liveData: MutableLiveData<Result<T>>): Flow<T> {
    return this
        .onStart {

        }
        .onEach {
            liveData.postValue(Result.success(it))
        }
        .catch {
            liveData.postValue(Result.failure(it))
        }
}