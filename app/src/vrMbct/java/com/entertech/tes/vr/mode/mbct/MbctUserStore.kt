package com.entertech.tes.vr.mode.mbct

import android.content.Context

data class MbctUser(
    val fullName: String,
    val username: String,
    val phone: String,
    val organization: String
)

object MbctUserStore {
    private const val PREFS_NAME = "vr_mbct_user_store"
    private const val KEY_FULL_NAME = "full_name"
    private const val KEY_USERNAME = "username"
    private const val KEY_PHONE = "phone"
    private const val KEY_ORGANIZATION = "organization"
    private const val KEY_PASSWORD = "password"
    private const val KEY_LOGGED_IN = "logged_in"

    fun isRegistered(context: Context): Boolean {
        val prefs = prefs(context)
        return prefs.getString(KEY_USERNAME, null)?.isNotBlank() == true &&
            prefs.getString(KEY_PASSWORD, null)?.isNotBlank() == true
    }

    fun isLoggedIn(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_LOGGED_IN, false) && isRegistered(context)
    }

    fun register(
        context: Context,
        fullName: String,
        username: String,
        phone: String,
        organization: String,
        password: String
    ) {
        prefs(context).edit()
            .putString(KEY_FULL_NAME, fullName)
            .putString(KEY_USERNAME, username)
            .putString(KEY_PHONE, phone)
            .putString(KEY_ORGANIZATION, organization)
            .putString(KEY_PASSWORD, password)
            .putBoolean(KEY_LOGGED_IN, true)
            .apply()
    }

    fun login(context: Context, account: String, password: String): Boolean {
        val prefs = prefs(context)
        val savedPassword = prefs.getString(KEY_PASSWORD, "").orEmpty()
        val savedUsername = prefs.getString(KEY_USERNAME, "").orEmpty()
        val savedPhone = prefs.getString(KEY_PHONE, "").orEmpty()
        val accountMatched = account == savedUsername || account == savedPhone
        if (!accountMatched || savedPassword != password) {
            return false
        }
        prefs.edit().putBoolean(KEY_LOGGED_IN, true).apply()
        return true
    }

    fun logout(context: Context) {
        prefs(context).edit().putBoolean(KEY_LOGGED_IN, false).apply()
    }

    fun getUser(context: Context): MbctUser? {
        if (!isRegistered(context)) {
            return null
        }
        val prefs = prefs(context)
        return MbctUser(
            fullName = prefs.getString(KEY_FULL_NAME, "").orEmpty(),
            username = prefs.getString(KEY_USERNAME, "").orEmpty(),
            phone = prefs.getString(KEY_PHONE, "").orEmpty(),
            organization = prefs.getString(KEY_ORGANIZATION, "").orEmpty()
        )
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
