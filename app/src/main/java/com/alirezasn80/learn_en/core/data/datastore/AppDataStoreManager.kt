package com.alirezasn80.learn_en.core.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private const val APP_DATASTORE = "app"

class AppDataStoreManager(
    private val context: Context,
) : AppDataStore {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(APP_DATASTORE)

    // Getter and setter boolean
    private suspend fun getBooleanValue(key: String, default: Boolean?) =
        context.dataStore.data.first()[booleanPreferencesKey(key)] ?: default

    private suspend fun setBooleanValue(key: String, value: Boolean) {
        context.dataStore.edit {
            it[booleanPreferencesKey(key)] = value
        }
    }

    private suspend fun setLongValue(key: String, value: Long) {
        context.dataStore.edit {
            it[longPreferencesKey(key)] = value
        }
    }

    private suspend fun getLongValue(key: String, default: Long?) =
        context.dataStore.data.first()[longPreferencesKey(key)] ?: default

    // Getter and setter int
    private suspend fun getIntValue(key: String) =
        context.dataStore.data.first()[intPreferencesKey(key)] ?: 0

    private suspend fun setIntValue(key: String, value: Int) {
        context.dataStore.edit { it[intPreferencesKey(key)] = value }
    }

    // Getter and setter string
    private suspend fun setStringValue(key: String, value: String) {
        context.dataStore.edit {
            it[stringPreferencesKey(key)] = value
        }
    }

    private suspend fun getStringValue(key: String) =
        context.dataStore.data.first()[stringPreferencesKey(key)]


    // Getter and setter onboarding
    override suspend fun showOnboarding(key: String, value: Boolean) {
        setBooleanValue(key, value)
    }

    override suspend fun showOnboarding(key: String): Boolean = getBooleanValue(key, true)!!


    // Getter and setter comment
    override suspend fun setCommentStatus(key: String, value: String) {
        setStringValue(key, value)
    }

    override suspend fun getCommentStatus(key: String): String? = getStringValue(key)


    // Getter and setter open app counter
    override suspend fun setOpenAppCounter(key: String, value: Int) {
        setIntValue(key, value)
    }

    override suspend fun getOpenAppCounter(key: String): Int = getIntValue(key)


    // Getter and setter Cleaner Permission
    override suspend fun validPermission(key: String, value: Boolean) {
        setBooleanValue(key, value)
    }

    override suspend fun isValidPermission(key: String): Boolean = getBooleanValue(key, false)!!


    override suspend fun isVIP(key: String, value: Boolean) {
        setBooleanValue(key, value)
    }

    override suspend fun isVIP(key: String): Boolean? = getBooleanValue(key, null)


    override suspend fun setExpireDate(key: String, value: Long) {
        setLongValue(key, value)
    }

    override suspend fun getExpireDate(key: String): Long? {
        return getLongValue(key, null)
    }


    override suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }

}
