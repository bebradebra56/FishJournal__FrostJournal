package com.fishjorunal.sofircl.lpnyht.data.utils

import android.util.Log
import com.fishjorunal.sofircl.lpnyht.presentation.app.FrostJournalApplication
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class FrostJournalPushToken {

    suspend fun frostJournalGetToken(
        frostJournalMaxAttempts: Int = 3,
        frostJournalDelayMs: Long = 1500
    ): String {

        repeat(frostJournalMaxAttempts - 1) {
            try {
                val frostJournalToken = FirebaseMessaging.getInstance().token.await()
                return frostJournalToken
            } catch (e: Exception) {
                Log.e(FrostJournalApplication.FROST_JOURNAL_MAIN_TAG, "Token error (attempt ${it + 1}): ${e.message}")
                delay(frostJournalDelayMs)
            }
        }

        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e(FrostJournalApplication.FROST_JOURNAL_MAIN_TAG, "Token error final: ${e.message}")
            "null"
        }
    }


}