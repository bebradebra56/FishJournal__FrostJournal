package com.fishjorunal.sofircl.lpnyht.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class FrostJournalEntity (
    @SerialName("ok")
    val frostJournalOk: Boolean,
    @SerialName("url")
    val frostJournalUrl: String,
    @SerialName("expires")
    val frostJournalExpires: Long,
)