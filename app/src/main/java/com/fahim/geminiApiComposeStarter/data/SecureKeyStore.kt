package com.fahim.geminiApiComposeStarter.data

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Stores the Gemini API key encrypted at rest, using a key kept inside the Android Keystore (AES-256-GCM). The plaintext key only ever exists in memory, briefly, when [getApiKey] or [saveApiKey] is called.
 */
class SecureKeyStore(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "secure_key_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    /** Saves [apiKey] encrypted. Call this once, e.g. the first time the app has a key to store. */
    fun saveApiKey(apiKey: String) {
        prefs.edit().putString(KEY_NAME, apiKey).apply()
    }

    /** Returns the decrypted key, or null if nothing has been saved yet. */
    fun getApiKey(): String? = prefs.getString(KEY_NAME, null)

    fun hasApiKey(): Boolean = !getApiKey().isNullOrBlank()

    private companion object {
        const val KEY_NAME = "gemini_api_key"
    }
}
