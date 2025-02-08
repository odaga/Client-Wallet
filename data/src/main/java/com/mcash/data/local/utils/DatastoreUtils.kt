package com.mcash.data.local.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mcash.data.UserProto
import com.mcash.data.local.serializers.UserPreferenceSerializer
import com.mcash.data.local.utils.LocalConstants.APP_PREFERENCES_NAME

val Context.userProtoDataStore: DataStore<UserProto> by dataStore(
    fileName = LocalConstants.USER_PREF_DB,
    serializer = UserPreferenceSerializer
)

val Context.appThemeDatastore by preferencesDataStore(
    name = APP_PREFERENCES_NAME
)

val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")

val IS_SYSTEM_DEFAULT  = booleanPreferencesKey("is_system_default")

val CART_COUNT = intPreferencesKey("cart_count")

val ACCOUNT_BALANCE = longPreferencesKey("account_balance")

val NWSC_AREAS = stringSetPreferencesKey("nwsc_areas")

val HELP_INFO = stringPreferencesKey("help_info")

val IS_SIGNED_IN = booleanPreferencesKey("is_signed_in")

val SESSION_JOB_ID = stringPreferencesKey("session_job_id")