package com.example.githubapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubapp.data.api.ApiConfig
import com.example.githubapp.data.response.UserResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FollowViewModel(username: String): ViewModel() {
    private val _followers = MutableLiveData<ArrayList<UserResponse>?>()
    val followers: LiveData<ArrayList<UserResponse>?> = _followers
    private val _following = MutableLiveData<ArrayList<UserResponse>?>()
    val following: LiveData<ArrayList<UserResponse>?> = _following
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _isDataFailed = MutableLiveData<Boolean>()
    val isDataFailed: LiveData<Boolean> = _isDataFailed

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    init {
        viewModelScope.launch {
            getListFollowers(username)
            getListFollowing(username)
        }
        Log.i(TAG, "FollowsFragment is Created")
    }

    private suspend fun getListFollowers(username: String) {
        coroutineScope.launch {
            _isLoading.value = true
            val result = ApiConfig.getApiService().getListFollowers(username)
            try{
                _isLoading.value = false
                _followers.postValue(result)
            }catch (e: Exception){
                _isLoading.value = false
                _isDataFailed.value = true
                Log.e(TAG, "OnFailure: ${e.message.toString()}")
            }
        }
    }

    private suspend fun getListFollowing(username: String) {
        coroutineScope.launch {
            _isLoading.value = true
            val result = ApiConfig.getApiService().getListFollowing(username)
            try{
                _isLoading.value = false
                _following.postValue(result)
            }catch (e: Exception){
                _isLoading.value = false
                _isDataFailed.value = true
                Log.e(TAG, "OnFailure: ${e.message.toString()}")
            }
        }
    }
    companion object {
        private const val TAG = "FollowersAndFollowingViewModel"
    }
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}