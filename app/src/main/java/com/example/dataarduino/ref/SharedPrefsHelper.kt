package com.example.dataarduino.ref

import android.content.Context
import javax.inject.Inject

class SharedPrefsHelper @Inject constructor(context: Context) {

    var preference = context.getSharedPreferences("dagger-pref", Context.MODE_PRIVATE)
    var PREF_KEY_ACCESS_TOKEN = "access-token"

    fun saveString(key: String?, value: String?) {
        preference.edit().putString(key, value).apply()
    }

    fun getString(key: String?, defaultValue: String?): String? {
        return preference.getString(key, defaultValue)
    }

    fun deleteSavedString(key: String?) {
        preference.edit().remove(key).apply()
    }

}
