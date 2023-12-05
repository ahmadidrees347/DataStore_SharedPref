package com.datastore.sharedpref.data

import android.util.Log
import com.datastore.sharedpref.model.UserEntity
import com.datastore.sharedpref.utils.Resource
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.ObjectId

class RealmRepositoryImpl(val realm: Realm) : RealmRepository {
    override fun getAllUsers() =
        realm.query<UserEntity>().asFlow().map { it.list }

    override fun getUserById(id: ObjectId) =
        realm.query<UserEntity>(query = "id == $0", id).first().find()

    override fun getAllUsersData(): Flow<Resource<List<UserEntity>>> {
        return flow {
            emit(Resource.Loading())
            try {
                val users = realm.query<UserEntity>().find().map { it }
                emit(Resource.Success(users))
            } catch (e: Exception) {
                emit(Resource.Error("Error loading users", null))
            }
        }
    }

    override fun filterUsers(name: String): Flow<List<UserEntity>> {
        return realm.query<UserEntity>(query = "name CONTAINS[c] $0", name).asFlow().map { it.list }
    }

    override suspend fun insertUser(user: UserEntity) {
        realm.write { copyToRealm(user) }
    }

    override suspend fun updateUser(user: UserEntity) {
        realm.write {
            val queriedUser = query<UserEntity>(query = "id == $0", user.id).first().find()
            queriedUser?.name = user.name
            queriedUser?.age = user.age
            queriedUser?.address = user.address
        }
    }

    override suspend fun deleteUser(id: ObjectId) {
        realm.write {
            val user = query<UserEntity>(query = "id == $0", id).first().find()
            try {
                user?.let { delete(it) }
            } catch (e: Exception) {
                Log.d("RealmRepositoryImpl", "${e.message}")
            }
        }
    }
}