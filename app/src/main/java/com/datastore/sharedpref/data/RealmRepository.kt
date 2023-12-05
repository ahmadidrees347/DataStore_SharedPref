package com.datastore.sharedpref.data

import com.datastore.sharedpref.model.UserEntity
import com.datastore.sharedpref.utils.Resource
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId

interface RealmRepository {
    fun getAllUsers(): Flow<List<UserEntity>>
    fun getUserById(id: ObjectId): UserEntity?
    fun getAllUsersData(): Flow<Resource<List<UserEntity>>>
    fun filterUsers(name: String): Flow<List<UserEntity>>
    suspend fun insertUser(user: UserEntity)
    suspend fun updateUser(user: UserEntity)
    suspend fun deleteUser(id: ObjectId)
}