package com.datastore.sharedpref.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Index
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class UserEntity : RealmObject {
    @PrimaryKey
    var id: ObjectId = ObjectId.invoke()
    var name: String = ""
    @Index
    var age: Int = 18
    var address: RealmList<UserAddress> = realmListOf()
    private var timestamp: RealmInstant = RealmInstant.now()
    override fun toString(): String {
        return "UserEntity(id=$id, name='$name', age='$age', address=$address, timestamp=$timestamp)"
    }
}

class UserAddress : EmbeddedRealmObject {
    var houseAddress: String = ""
    var regionName: String = ""

    override fun toString(): String {
        return "UserAddress(houseAddress='$houseAddress', regionName='$regionName')"
    }

}