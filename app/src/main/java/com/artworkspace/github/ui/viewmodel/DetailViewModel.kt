package com.artworkspace.github.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artworkspace.github.BuildConfig
import com.artworkspace.github.data.UserRepository
import com.artworkspace.github.data.local.entity.UserEntity
import com.artworkspace.github.data.remote.response.User
import com.artworkspace.github.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel(private val repository: UserRepository) : ViewModel() {
    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _callCounter = MutableLiveData(0)
    val callCounter: LiveData<Int> = _callCounter

    private val _isError = MutableLiveData(false)
    val isError: LiveData<Boolean> = _isError

    private val _user = MutableLiveData<User?>(null)
    val user: LiveData<User?> = _user

    /**
     * Save user to database as favorite user
     *
     * @param user New favorite user
     */
    fun saveAsFavorite(user: UserEntity) {
        viewModelScope.launch {
            repository.saveUserAsFavorite(user)
        }
    }

    /**
     * Delete favorite user from database
     *
     * @param user User to delete
     */
    fun deleteFromFavorite(user: UserEntity) {
        viewModelScope.launch {
            repository.deleteFromFavorite(user)
        }
    }

    /**
     * Determine this is favorite user or not
     *
     * @param id User id
     * @return LiveData<Boolean>
     */
    fun isFavoriteUser(id: String): LiveData<Boolean> = repository.isFavoriteUser(id)

    /**
     *  Get user detail information
     *
     *  @param username GitHub username
     *  @return Unit
     */
    fun getUserDetail(username: String) {
        _isLoading.value = true
        _callCounter.value = 1

        ApiConfig.getApiService().getUserDetail(token = "Bearer ${BuildConfig.API_KEY}", username)
            .apply {
                enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.isSuccessful) _user.value = response.body()
                        else Log.e(TAG, response.message())

                        _isLoading.value = false
                        _isError.value = false
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.e(TAG, t.message.toString())

                    _isLoading.value = false
                    _isError.value = true
                }

            })
        }
    }

    companion object {
        private val TAG = DetailViewModel::class.java.simpleName
    }
}