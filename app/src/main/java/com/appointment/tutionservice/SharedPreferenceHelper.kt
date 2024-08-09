package com.appointment.tutionservice

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceHelper(val context: Context) {

    companion object {
        private const val PREF_NAME = "LoginPreference"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val APP_USER_KEY = "userId"
        private const val REGISTRATION_ID = "registrationId"
        private const val USER_ACTION_TYPE = "actionType"
        private const val APP_USER_TYPE = "userType"
        private const val APP_USER_ID = "userID"
        private const val MOBILE_NO = "mobileNo"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun saveLoginData(appUserKey: String, registrationId: String, actionType: String, userType: String, userId: String, mobileNo: String) {
        val editor = sharedPreferences.edit()
        editor.putString(APP_USER_KEY, appUserKey)
        editor.putString(REGISTRATION_ID, registrationId)
        editor.putString(USER_ACTION_TYPE, actionType)
        editor.putString(APP_USER_TYPE, userType)
        editor.putString(APP_USER_ID, userId)
        editor.putString(MOBILE_NO, mobileNo)
        editor.apply()
    }

    fun getUserKey(context: Context): String {
        return getSharedPreferences(context).getString(APP_USER_KEY, "") ?: ""
    }

    fun getRegistrationId(context: Context): String {
        return getSharedPreferences(context).getString(REGISTRATION_ID, "") ?: ""
    }

    fun getUserActionType(context: Context): String {
        return getSharedPreferences(context).getString(USER_ACTION_TYPE, "") ?: ""
    }

    fun getUserType(context: Context): String {
        return getSharedPreferences(context).getString(APP_USER_TYPE, "") ?: ""
    }

    fun getUserId(context: Context): String {
        return getSharedPreferences(context).getString(APP_USER_ID, "") ?: ""
    }

    fun getMobileNo(context: Context): String {
        return getSharedPreferences(context).getString(MOBILE_NO, "") ?: ""
    }

    fun clearSession() {
        val editor = sharedPreferences.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, false)
        editor.putString(APP_USER_KEY, "")
        editor.putString(REGISTRATION_ID, "")
        editor.putString(USER_ACTION_TYPE, "")
        editor.putString(APP_USER_TYPE, "")
        editor.putString(APP_USER_ID, "")
        editor.putString(MOBILE_NO, "")
        editor.apply()
    }
}