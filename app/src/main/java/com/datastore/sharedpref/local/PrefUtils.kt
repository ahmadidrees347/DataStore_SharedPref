package com.datastore.sharedpref.local


import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.core.os.UserManagerCompat
import androidx.datastore.preferences.core.stringSetPreferencesKey

@Suppress("UNCHECKED_CAST")
class PrefUtils(context: Context) {

    private val localDb = "shared_prefs"

    private val shared: SharedPreferences =
        if (!UserManagerCompat.isUserUnlocked(context) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            context.createDeviceProtectedStorageContext()
                .getSharedPreferences(localDb, Context.MODE_PRIVATE)
        else
            context.getSharedPreferences(localDb, Context.MODE_PRIVATE)


    /**
     * Gets the value for given [key]. The type is automatically derived from the given [default] value.
     *
     * @return The value for [key] or [default].
     */
    private inline fun <reified T> getPref(key: String, default: T): T {
        return when (default) {
            is Int -> (shared.getInt(key, default as Int) as T)
            is Long -> (shared.getLong(key, default as Long) as T)
            is Float -> (shared.getFloat(key, default as Float) as T)
            is Boolean -> (shared.getBoolean(key, default as Boolean) as T)
            is String -> ((shared.getString(key, default as String) ?: (default as String)) as T)
            is Set<*> -> (shared.getStringSet(key, default as Set<String>) as T)
            else -> default
        }
    }

    /**
     * Sets the [value] for [key] in the shared preferences, puts the value into the corresponding
     * cache and returns it.
     */
    private inline fun <reified T> setPref(key: String, value: T) {
        when (value) {
            is Int -> (shared.edit().putInt(key, value as Int).apply())
            is Long -> (shared.edit().putLong(key, value as Long).apply())
            is Float -> (shared.edit().putFloat(key, value as Float).apply())
            is Boolean -> (shared.edit().putBoolean(key, value as Boolean).apply())
            is String -> ((shared.edit().putString(key, value as String).apply()))
            is Set<*> -> (shared.edit().putStringSet(key, value as Set<String>).apply())
        }
    }

    var isFirstTime: Boolean
        get() = getPref(Constant.IS_PIN_SET, true)
        set(v) = setPref(Constant.IS_PIN_SET, v)

    var pinCode: String
        get() = getPref(Constant.PIN_CODE, "")
        set(v) = setPref(Constant.PIN_CODE, v)
}