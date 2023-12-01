package com.datastore.sharedpref.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class DataStoreUtils(val context: Context) {
    private val localDb = "db_preferences"

    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = localDb,
//        produceMigrations = { context ->
//            listOf(SharedPreferencesMigration(context, localDb))
//        }
    )

    suspend fun clearDataStore() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    inline fun <reified T> getValue(key: String, defaultValue: T): Flow<T> {
        return context.dataStore.data.map { preferences ->
            when (defaultValue) {
                is Int -> (preferences[intPreferencesKey(key)] ?: defaultValue) as T
                is Long -> (preferences[longPreferencesKey(key)] ?: defaultValue) as T
                is Float -> (preferences[floatPreferencesKey(key)] ?: defaultValue) as T
                is Double -> (preferences[doublePreferencesKey(key)] ?: defaultValue) as T
                is Boolean -> (preferences[booleanPreferencesKey(key)] ?: defaultValue) as T
                is String -> (preferences[stringPreferencesKey(key)] ?: defaultValue) as T
                is Set<*> -> (preferences[stringSetPreferencesKey(key)] ?: defaultValue) as T
                else -> defaultValue
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    suspend inline fun <reified T> setValue(key: String, value: T) {
        context.dataStore.edit { preferences ->
            when (value) {
                is Int -> preferences[intPreferencesKey(key)] = value
                is Long -> preferences[longPreferencesKey(key)] = value
                is Float -> preferences[floatPreferencesKey(key)] = value
                is Double -> preferences[doublePreferencesKey(key)] = value
                is Boolean -> preferences[booleanPreferencesKey(key)] = value
                is String -> preferences[stringPreferencesKey(key)] = value
                is Set<*> -> preferences[stringSetPreferencesKey(key)] = value as Set<String>
            }
        }
    }

    var isFirstTime: Flow<Boolean>
        get() = getValue(Constant.IS_PIN_SET, defaultValue = true)
        set(value) = runBlocking {
            launch {
                setValue(Constant.IS_PIN_SET, value.first())
            }
        }

    var pinCode: Flow<String>
        get() = getValue(Constant.PIN_CODE, defaultValue = "")
        set(value) = runBlocking {
            launch {
                setValue(Constant.PIN_CODE, value.first())
            }
        }

    var stringSetExample: Flow<Set<String>>
        get() = getValue(Constant.STRING_SET_KEY, defaultValue = setOf())
        set(value) = runBlocking {
            launch {
                setValue(Constant.STRING_SET_KEY, value.first())
            }
        }


}

