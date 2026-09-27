package com.fahim.geminiApiComposeStarter.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

/** Remembers simple user preferences, e.g. whether they've seen the voice-input hint. */
class UserPreferences(private val context: Context) {

    private val hasSeenVoiceHintKey = booleanPreferencesKey("has_seen_voice_hint")

    val hasSeenVoiceHint: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[hasSeenVoiceHintKey] ?: false
    }

    suspend fun setHasSeenVoiceHint(value: Boolean) {
        context.dataStore.edit { it[hasSeenVoiceHintKey] = value }
    }
}
