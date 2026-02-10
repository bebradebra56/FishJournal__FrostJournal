package com.fishjorunal.sofircl.lpnyht.presentation.pushhandler

import android.os.Bundle
import android.util.Log
import com.fishjorunal.sofircl.lpnyht.presentation.app.FrostJournalApplication

class FrostJournalPushHandler {
    fun frostJournalHandlePush(extras: Bundle?) {
        Log.d(FrostJournalApplication.FROST_JOURNAL_MAIN_TAG, "Extras from Push = ${extras?.keySet()}")
        if (extras != null) {
            val map: MutableMap<String, String?> = HashMap()
            val ks = extras.keySet()
            val iterator: Iterator<String> = ks.iterator()
            while (iterator.hasNext()) {
                val key = iterator.next()
                map[key] = extras.getString(key)
            }
            Log.d(FrostJournalApplication.FROST_JOURNAL_MAIN_TAG, "Map from Push = $map")
            map.let {
                if (map.containsKey("url")) {
                    FrostJournalApplication.FROST_JOURNAL_FB_LI = map["url"]
                    Log.d(FrostJournalApplication.FROST_JOURNAL_MAIN_TAG, "UrlFromActivity = $map")
                }
            }
        } else {
            Log.d(FrostJournalApplication.FROST_JOURNAL_MAIN_TAG, "Push data no!")
        }
    }

}