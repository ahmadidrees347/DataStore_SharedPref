package com.datastore.sharedpref.di

import com.datastore.sharedpref.data.RealmRepository
import com.datastore.sharedpref.data.RealmRepositoryImpl
import com.datastore.sharedpref.model.UserAddress
import com.datastore.sharedpref.model.UserEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import java.security.SecureRandom
import javax.inject.Singleton
import kotlin.random.Random

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    private const val DB_VERSION: Long = 1L

    @Singleton
    @Provides
    fun provideRealm(): Realm {
        val config = RealmConfiguration
            .Builder(schema = setOf(UserEntity::class, UserAddress::class))
//            .encryptionKey(generate64ByteKey())
//            .schemaVersion(DB_VERSION)
            .deleteRealmIfMigrationNeeded()
            .compactOnLaunch()
            .build()
        return Realm.open(config)
    }

    @Singleton
    @Provides
    fun provideRealmRepository(realm: Realm): RealmRepository {
        return RealmRepositoryImpl(realm = realm)
    }

    fun generate64ByteKey(): ByteArray {
        val secureRandom = SecureRandom()
        val key = ByteArray(64)
        secureRandom.nextBytes(key)
        return key
    }

    private fun getRandomKey(seed: Long? = null): ByteArray {
        // generate a new 64-byte encryption key
        val key = ByteArray(64)
        if (seed != null) {
            // If there is a seed provided, create a random number with that seed
            // and fill the byte array with random bytes
            Random(seed).nextBytes(key)
        } else {
            // fill the byte array with random bytes
            Random.nextBytes(key)
        }
        return key
    }
}