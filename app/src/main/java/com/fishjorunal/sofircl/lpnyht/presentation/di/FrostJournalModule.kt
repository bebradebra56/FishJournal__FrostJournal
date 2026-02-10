package com.fishjorunal.sofircl.lpnyht.presentation.di

import com.fishjorunal.sofircl.lpnyht.data.repo.FrostJournalRepository
import com.fishjorunal.sofircl.lpnyht.data.shar.FrostJournalSharedPreference
import com.fishjorunal.sofircl.lpnyht.data.utils.FrostJournalPushToken
import com.fishjorunal.sofircl.lpnyht.data.utils.FrostJournalSystemService
import com.fishjorunal.sofircl.lpnyht.domain.usecases.FrostJournalGetAllUseCase
import com.fishjorunal.sofircl.lpnyht.presentation.pushhandler.FrostJournalPushHandler
import com.fishjorunal.sofircl.lpnyht.presentation.ui.load.FrostJournalLoadViewModel
import com.fishjorunal.sofircl.lpnyht.presentation.ui.view.FrostJournalViFun
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val frostJournalModule = module {
    factory {
        FrostJournalPushHandler()
    }
    single {
        FrostJournalRepository()
    }
    single {
        FrostJournalSharedPreference(get())
    }
    factory {
        FrostJournalPushToken()
    }
    factory {
        FrostJournalSystemService(get())
    }
    factory {
        FrostJournalGetAllUseCase(
            get(), get(), get()
        )
    }
    factory {
        FrostJournalViFun(get())
    }
    viewModel {
        FrostJournalLoadViewModel(get(), get(), get())
    }
}