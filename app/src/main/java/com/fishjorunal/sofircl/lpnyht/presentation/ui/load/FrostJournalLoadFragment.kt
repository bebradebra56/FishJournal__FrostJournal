package com.fishjorunal.sofircl.lpnyht.presentation.ui.load

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.fishjorunal.sofircl.MainActivity
import com.fishjorunal.sofircl.R
import com.fishjorunal.sofircl.databinding.FragmentLoadFrostJournalBinding
import com.fishjorunal.sofircl.lpnyht.data.shar.FrostJournalSharedPreference
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class FrostJournalLoadFragment : Fragment(R.layout.fragment_load_frost_journal) {
    private lateinit var frostJournalLoadBinding: FragmentLoadFrostJournalBinding

    private val frostJournalLoadViewModel by viewModel<FrostJournalLoadViewModel>()

    private val frostJournalSharedPreference by inject<FrostJournalSharedPreference>()

    private var frostJournalUrl = ""

    private val frostJournalRequestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        frostJournalSharedPreference.frostJournalNotificationState = 2
        frostJournalNavigateToSuccess(frostJournalUrl)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        frostJournalLoadBinding = FragmentLoadFrostJournalBinding.bind(view)

        frostJournalLoadBinding.frostJournalGrandButton.setOnClickListener {
            val frostJournalPermission = Manifest.permission.POST_NOTIFICATIONS
            frostJournalRequestNotificationPermission.launch(frostJournalPermission)
        }

        frostJournalLoadBinding.frostJournalSkipButton.setOnClickListener {
            frostJournalSharedPreference.frostJournalNotificationState = 1
            frostJournalSharedPreference.frostJournalNotificationRequest =
                (System.currentTimeMillis() / 1000) + 259200
            frostJournalNavigateToSuccess(frostJournalUrl)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                frostJournalLoadViewModel.frostJournalHomeScreenState.collect {
                    when (it) {
                        is FrostJournalLoadViewModel.FrostJournalHomeScreenState.FrostJournalLoading -> {

                        }

                        is FrostJournalLoadViewModel.FrostJournalHomeScreenState.FrostJournalError -> {
                            requireActivity().startActivity(
                                Intent(
                                    requireContext(),
                                    MainActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        }

                        is FrostJournalLoadViewModel.FrostJournalHomeScreenState.FrostJournalSuccess -> {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                                val frostJournalNotificationState = frostJournalSharedPreference.frostJournalNotificationState
                                when (frostJournalNotificationState) {
                                    0 -> {
                                        frostJournalLoadBinding.frostJournalNotiGroup.visibility = View.VISIBLE
                                        frostJournalLoadBinding.frostJournalLoadingGroup.visibility = View.GONE
                                        frostJournalUrl = it.data
                                    }
                                    1 -> {
                                        if (System.currentTimeMillis() / 1000 > frostJournalSharedPreference.frostJournalNotificationRequest) {
                                            frostJournalLoadBinding.frostJournalNotiGroup.visibility = View.VISIBLE
                                            frostJournalLoadBinding.frostJournalLoadingGroup.visibility = View.GONE
                                            frostJournalUrl = it.data
                                        } else {
                                            frostJournalNavigateToSuccess(it.data)
                                        }
                                    }
                                    2 -> {
                                        frostJournalNavigateToSuccess(it.data)
                                    }
                                }
                            } else {
                                frostJournalNavigateToSuccess(it.data)
                            }
                        }

                        FrostJournalLoadViewModel.FrostJournalHomeScreenState.FrostJournalNotInternet -> {
                            frostJournalLoadBinding.frostJournalStateGroup.visibility = View.VISIBLE
                            frostJournalLoadBinding.frostJournalLoadingGroup.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun frostJournalNavigateToSuccess(data: String) {
        findNavController().navigate(
            R.id.action_frostJournalLoadFragment_to_frostJournalV,
            bundleOf(FROST_JOURNAL_D to data)
        )
    }

    companion object {
        const val FROST_JOURNAL_D = "frostJournalData"
    }
}