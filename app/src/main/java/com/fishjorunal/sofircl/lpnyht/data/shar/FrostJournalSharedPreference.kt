package com.fishjorunal.sofircl.lpnyht.data.shar

import android.content.Context
import androidx.core.content.edit

class FrostJournalSharedPreference(context: Context) {
    private val frostJournalPrefs = context.getSharedPreferences("frostJournalSharedPrefsAb", Context.MODE_PRIVATE)

    var frostJournalSavedUrl: String
        get() = frostJournalPrefs.getString(FROST_JOURNAL_SAVED_URL, "") ?: ""
        set(value) = frostJournalPrefs.edit { putString(FROST_JOURNAL_SAVED_URL, value) }

    var frostJournalExpired : Long
        get() = frostJournalPrefs.getLong(FROST_JOURNAL_EXPIRED, 0L)
        set(value) = frostJournalPrefs.edit { putLong(FROST_JOURNAL_EXPIRED, value) }

    var frostJournalAppState: Int
        get() = frostJournalPrefs.getInt(FROST_JOURNAL_APPLICATION_STATE, 0)
        set(value) = frostJournalPrefs.edit { putInt(FROST_JOURNAL_APPLICATION_STATE, value) }

    var frostJournalNotificationRequest: Long
        get() = frostJournalPrefs.getLong(FROST_JOURNAL_NOTIFICAITON_REQUEST, 0L)
        set(value) = frostJournalPrefs.edit { putLong(FROST_JOURNAL_NOTIFICAITON_REQUEST, value) }

    var frostJournalNotificationState:Int
        get() = frostJournalPrefs.getInt(FROST_JOURNAL_NOTIFICATION_STATE, 0)
        set(value) = frostJournalPrefs.edit { putInt(FROST_JOURNAL_NOTIFICATION_STATE, value) }

    companion object {
        private const val FROST_JOURNAL_NOTIFICATION_STATE = "frostJournalNotificationState"
        private const val FROST_JOURNAL_SAVED_URL = "frostJournalSavedUrl"
        private const val FROST_JOURNAL_EXPIRED = "frostJournalExpired"
        private const val FROST_JOURNAL_APPLICATION_STATE = "frostJournalApplicationState"
        private const val FROST_JOURNAL_NOTIFICAITON_REQUEST = "frostJournalNotificationRequest"
    }
}