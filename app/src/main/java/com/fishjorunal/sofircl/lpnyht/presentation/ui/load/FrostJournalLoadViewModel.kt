package com.fishjorunal.sofircl.lpnyht.presentation.ui.load

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fishjorunal.sofircl.lpnyht.data.shar.FrostJournalSharedPreference
import com.fishjorunal.sofircl.lpnyht.data.utils.FrostJournalSystemService
import com.fishjorunal.sofircl.lpnyht.domain.usecases.FrostJournalGetAllUseCase
import com.fishjorunal.sofircl.lpnyht.presentation.app.FrostJournalAppsFlyerState
import com.fishjorunal.sofircl.lpnyht.presentation.app.FrostJournalApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FrostJournalLoadViewModel(
    private val frostJournalGetAllUseCase: FrostJournalGetAllUseCase,
    private val frostJournalSharedPreference: FrostJournalSharedPreference,
    private val frostJournalSystemService: FrostJournalSystemService
) : ViewModel() {

    private val _frostJournalHomeScreenState: MutableStateFlow<FrostJournalHomeScreenState> =
        MutableStateFlow(FrostJournalHomeScreenState.FrostJournalLoading)
    val frostJournalHomeScreenState = _frostJournalHomeScreenState.asStateFlow()

    private var frostJournalGetApps = false


    init {
        viewModelScope.launch {
            when (frostJournalSharedPreference.frostJournalAppState) {
                0 -> {
                    if (frostJournalSystemService.frostJournalIsOnline()) {
                        FrostJournalApplication.frostJournalConversionFlow.collect {
                            when(it) {
                                FrostJournalAppsFlyerState.FrostJournalDefault -> {}
                                FrostJournalAppsFlyerState.FrostJournalError -> {
                                    frostJournalSharedPreference.frostJournalAppState = 2
                                    _frostJournalHomeScreenState.value =
                                        FrostJournalHomeScreenState.FrostJournalError
                                    frostJournalGetApps = true
                                }
                                is FrostJournalAppsFlyerState.FrostJournalSuccess -> {
                                    if (!frostJournalGetApps) {
                                        frostJournalGetData(it.frostJournalData)
                                        frostJournalGetApps = true
                                    }
                                }
                            }
                        }
                    } else {
                        _frostJournalHomeScreenState.value =
                            FrostJournalHomeScreenState.FrostJournalNotInternet
                    }
                }
                1 -> {
                    if (frostJournalSystemService.frostJournalIsOnline()) {
                        if (FrostJournalApplication.FROST_JOURNAL_FB_LI != null) {
                            _frostJournalHomeScreenState.value =
                                FrostJournalHomeScreenState.FrostJournalSuccess(
                                    FrostJournalApplication.FROST_JOURNAL_FB_LI.toString()
                                )
                        } else if (System.currentTimeMillis() / 1000 > frostJournalSharedPreference.frostJournalExpired) {
                            Log.d(FrostJournalApplication.FROST_JOURNAL_MAIN_TAG, "Current time more then expired, repeat request")
                            FrostJournalApplication.frostJournalConversionFlow.collect {
                                when(it) {
                                    FrostJournalAppsFlyerState.FrostJournalDefault -> {}
                                    FrostJournalAppsFlyerState.FrostJournalError -> {
                                        _frostJournalHomeScreenState.value =
                                            FrostJournalHomeScreenState.FrostJournalSuccess(
                                                frostJournalSharedPreference.frostJournalSavedUrl
                                            )
                                        frostJournalGetApps = true
                                    }
                                    is FrostJournalAppsFlyerState.FrostJournalSuccess -> {
                                        if (!frostJournalGetApps) {
                                            frostJournalGetData(it.frostJournalData)
                                            frostJournalGetApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(FrostJournalApplication.FROST_JOURNAL_MAIN_TAG, "Current time less then expired, use saved url")
                            _frostJournalHomeScreenState.value =
                                FrostJournalHomeScreenState.FrostJournalSuccess(
                                    frostJournalSharedPreference.frostJournalSavedUrl
                                )
                        }
                    } else {
                        _frostJournalHomeScreenState.value =
                            FrostJournalHomeScreenState.FrostJournalNotInternet
                    }
                }
                2 -> {
                    _frostJournalHomeScreenState.value =
                        FrostJournalHomeScreenState.FrostJournalError
                }
            }
        }
    }


    private suspend fun frostJournalGetData(conversation: MutableMap<String, Any>?) {
        val frostJournalData = frostJournalGetAllUseCase.invoke(conversation)
        if (frostJournalSharedPreference.frostJournalAppState == 0) {
            if (frostJournalData == null) {
                frostJournalSharedPreference.frostJournalAppState = 2
                _frostJournalHomeScreenState.value =
                    FrostJournalHomeScreenState.FrostJournalError
            } else {
                frostJournalSharedPreference.frostJournalAppState = 1
                frostJournalSharedPreference.apply {
                    frostJournalExpired = frostJournalData.frostJournalExpires
                    frostJournalSavedUrl = frostJournalData.frostJournalUrl
                }
                _frostJournalHomeScreenState.value =
                    FrostJournalHomeScreenState.FrostJournalSuccess(frostJournalData.frostJournalUrl)
            }
        } else  {
            if (frostJournalData == null) {
                _frostJournalHomeScreenState.value =
                    FrostJournalHomeScreenState.FrostJournalSuccess(frostJournalSharedPreference.frostJournalSavedUrl)
            } else {
                frostJournalSharedPreference.apply {
                    frostJournalExpired = frostJournalData.frostJournalExpires
                    frostJournalSavedUrl = frostJournalData.frostJournalUrl
                }
                _frostJournalHomeScreenState.value =
                    FrostJournalHomeScreenState.FrostJournalSuccess(frostJournalData.frostJournalUrl)
            }
        }
    }


    sealed class FrostJournalHomeScreenState {
        data object FrostJournalLoading : FrostJournalHomeScreenState()
        data object FrostJournalError : FrostJournalHomeScreenState()
        data class FrostJournalSuccess(val data: String) : FrostJournalHomeScreenState()
        data object FrostJournalNotInternet: FrostJournalHomeScreenState()
    }
}