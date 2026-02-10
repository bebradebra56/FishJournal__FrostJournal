package com.fishjorunal.sofircl.lpnyht.presentation.ui.view

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel

class FrostJournalDataStore : ViewModel(){
    val frostJournalViList: MutableList<FrostJournalVi> = mutableListOf()
    var frostJournalIsFirstCreate = true
    @SuppressLint("StaticFieldLeak")
    lateinit var frostJournalContainerView: FrameLayout
    @SuppressLint("StaticFieldLeak")
    lateinit var frostJournalView: FrostJournalVi

}