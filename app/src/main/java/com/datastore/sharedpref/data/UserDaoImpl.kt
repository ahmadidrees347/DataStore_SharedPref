package com.datastore.sharedpref.data

import com.datastore.sharedpref.model.UserEntity
import io.realm.kotlin.Realm
import javax.inject.Inject
import kotlin.reflect.KClass

interface UserDao : RealmDao<UserEntity>

class UserDaoImpl @Inject constructor(r: Realm) : UserDao {
    override val realm: Realm = r
    override val clazz: KClass<UserEntity> = UserEntity::class
}