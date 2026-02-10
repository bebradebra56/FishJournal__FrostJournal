package com.fishjorunal.sofircl.lpnyht.domain.usecases

import android.util.Log
import com.fishjorunal.sofircl.lpnyht.data.repo.FrostJournalRepository
import com.fishjorunal.sofircl.lpnyht.data.utils.FrostJournalPushToken
import com.fishjorunal.sofircl.lpnyht.data.utils.FrostJournalSystemService
import com.fishjorunal.sofircl.lpnyht.domain.model.FrostJournalEntity
import com.fishjorunal.sofircl.lpnyht.domain.model.FrostJournalParam
import com.fishjorunal.sofircl.lpnyht.presentation.app.FrostJournalApplication

class FrostJournalGetAllUseCase(
    private val frostJournalRepository: FrostJournalRepository,
    private val frostJournalSystemService: FrostJournalSystemService,
    private val frostJournalPushToken: FrostJournalPushToken,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?) : FrostJournalEntity?{
        val params = FrostJournalParam(
            frostJournalLocale = frostJournalSystemService.frostJournalGetLocale(),
            frostJournalPushToken = frostJournalPushToken.frostJournalGetToken(),
            frostJournalAfId = frostJournalSystemService.frostJournalGetAppsflyerId()
        )
        Log.d(FrostJournalApplication.FROST_JOURNAL_MAIN_TAG, "Params for request: $params")
        return frostJournalRepository.frostJournalGetClient(params, conversion)
    }



}