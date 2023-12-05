package com.datastore.sharedpref.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.datastore.sharedpref.data.RealmRepository
import com.datastore.sharedpref.model.AllUserState
import com.datastore.sharedpref.model.SpecificUserState
import com.datastore.sharedpref.model.UserEntity
import com.datastore.sharedpref.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.mongodb.kbson.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: RealmRepository
) : ViewModel() {

    private val _userData = MutableStateFlow(AllUserState())
    var userData: StateFlow<AllUserState> = _userData

    private val _specificUserData = MutableStateFlow(SpecificUserState())
    var specificUserData: StateFlow<SpecificUserState> = _specificUserData

    fun insertUser(userEntity: UserEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertUser(userEntity)
        }
    }

    fun updateUser(userId: String, updatedValue: UserEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            if (userId.isNotEmpty()) {
                repository.updateUser(user = updatedValue)
            }
        }
    }

    fun deleteUser(id: String) {
        viewModelScope.launch {
            if (id.isNotEmpty()) {
                repository.deleteUser(id = ObjectId(hexString = id))
            }
        }
    }

    fun filterData(filteredValue: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            if (filteredValue.isNotEmpty())
                repository.filterUsers(name = filteredValue)
                    .collect {
                        _userData.value = AllUserState(userRecord = it.toMutableList())
                    }
            else
                repository.getAllUsers().collect {
                    _userData.value = AllUserState(userRecord = it.toMutableList())
                }
        }
    }

    fun getUserById(id: String) {
        viewModelScope.launch {
            repository.getUserById(id = ObjectId(hexString = id))?.let {
                _specificUserData.value = SpecificUserState(userRecord = it)
            }
        }
    }

    fun getAllUsers() {
        viewModelScope.launch {
            repository.getAllUsers().collectLatest {
                _userData.value =
                    AllUserState(userRecord = it.toMutableList())
            }
        }
    }

    fun getAllUsersData() {
        viewModelScope.launch {
            repository.getAllUsersData().collectLatest {
                Log.e("data**", "${it.data?.size.toString()} - ${it.message}")
                when (it) {
                    is Resource.Loading -> {
                        _userData.value = AllUserState(isLoading = true)
                    }

                    is Resource.Success -> {
                        _userData.value =
                            AllUserState(userRecord = it.data?.toMutableList())
                    }

                    is Resource.Error -> {
                        _userData.value =
                            AllUserState(error = it.message.toString())
                    }
                }
            }
        }

    }
}