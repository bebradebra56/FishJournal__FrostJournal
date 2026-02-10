package com.fishjorunal.sofircl.lpnyht.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


private const val FROST_JOURNAL_A = "com.fishjorunal.sofircl"
private const val FROST_JOURNAL_B = "fish-journal"
@Serializable
data class FrostJournalParam (
    @SerialName("af_id")
    val frostJournalAfId: String,
    @SerialName("bundle_id")
    val frostJournalBundleId: String = FROST_JOURNAL_A,
    @SerialName("os")
    val frostJournalOs: String = "Android",
    @SerialName("store_id")
    val frostJournalStoreId: String = FROST_JOURNAL_A,
    @SerialName("locale")
    val frostJournalLocale: String,
    @SerialName("push_token")
    val frostJournalPushToken: String,
    @SerialName("firebase_project_id")
    val frostJournalFirebaseProjectId: String = FROST_JOURNAL_B,
    )