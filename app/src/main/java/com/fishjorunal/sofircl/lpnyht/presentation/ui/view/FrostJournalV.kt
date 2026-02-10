package com.fishjorunal.sofircl.lpnyht.presentation.ui.view

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.fishjorunal.sofircl.lpnyht.presentation.app.FrostJournalApplication
import com.fishjorunal.sofircl.lpnyht.presentation.ui.load.FrostJournalLoadFragment
import org.koin.android.ext.android.inject

class FrostJournalV : Fragment(){

    private lateinit var frostJournalPhoto: Uri
    private var frostJournalFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    private val frostJournalTakeFile: ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        frostJournalFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
        frostJournalFilePathFromChrome = null
    }

    private val frostJournalTakePhoto: ActivityResultLauncher<Uri> = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            frostJournalFilePathFromChrome?.onReceiveValue(arrayOf(frostJournalPhoto))
            frostJournalFilePathFromChrome = null
        } else {
            frostJournalFilePathFromChrome?.onReceiveValue(null)
            frostJournalFilePathFromChrome = null
        }
    }

    private val frostJournalDataStore by activityViewModels<FrostJournalDataStore>()


    private val frostJournalViFun by inject<FrostJournalViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(FrostJournalApplication.FROST_JOURNAL_MAIN_TAG, "Fragment onCreate")
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (frostJournalDataStore.frostJournalView.canGoBack()) {
                        frostJournalDataStore.frostJournalView.goBack()
                        Log.d(FrostJournalApplication.FROST_JOURNAL_MAIN_TAG, "WebView can go back")
                    } else if (frostJournalDataStore.frostJournalViList.size > 1) {
                        Log.d(FrostJournalApplication.FROST_JOURNAL_MAIN_TAG, "WebView can`t go back")
                        frostJournalDataStore.frostJournalViList.removeAt(frostJournalDataStore.frostJournalViList.lastIndex)
                        Log.d(FrostJournalApplication.FROST_JOURNAL_MAIN_TAG, "WebView list size ${frostJournalDataStore.frostJournalViList.size}")
                        frostJournalDataStore.frostJournalView.destroy()
                        val previousWebView = frostJournalDataStore.frostJournalViList.last()
                        frostJournalAttachWebViewToContainer(previousWebView)
                        frostJournalDataStore.frostJournalView = previousWebView
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (frostJournalDataStore.frostJournalIsFirstCreate) {
            frostJournalDataStore.frostJournalIsFirstCreate = false
            frostJournalDataStore.frostJournalContainerView = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            return frostJournalDataStore.frostJournalContainerView
        } else {
            return frostJournalDataStore.frostJournalContainerView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(FrostJournalApplication.FROST_JOURNAL_MAIN_TAG, "onViewCreated")
        if (frostJournalDataStore.frostJournalViList.isEmpty()) {
            frostJournalDataStore.frostJournalView = FrostJournalVi(requireContext(), object :
                FrostJournalCallBack {
                override fun frostJournalHandleCreateWebWindowRequest(frostJournalVi: FrostJournalVi) {
                    frostJournalDataStore.frostJournalViList.add(frostJournalVi)
                    Log.d(FrostJournalApplication.FROST_JOURNAL_MAIN_TAG, "WebView list size = ${frostJournalDataStore.frostJournalViList.size}")
                    Log.d(FrostJournalApplication.FROST_JOURNAL_MAIN_TAG, "CreateWebWindowRequest")
                    frostJournalDataStore.frostJournalView = frostJournalVi
                    frostJournalVi.frostJournalSetFileChooserHandler { callback ->
                        frostJournalHandleFileChooser(callback)
                    }
                    frostJournalAttachWebViewToContainer(frostJournalVi)
                }

            }, frostJournalWindow = requireActivity().window).apply {
                frostJournalSetFileChooserHandler { callback ->
                    frostJournalHandleFileChooser(callback)
                }
            }
            frostJournalDataStore.frostJournalView.frostJournalFLoad(arguments?.getString(
                FrostJournalLoadFragment.FROST_JOURNAL_D) ?: "")
//            ejvview.fLoad("www.google.com")
            frostJournalDataStore.frostJournalViList.add(frostJournalDataStore.frostJournalView)
            frostJournalAttachWebViewToContainer(frostJournalDataStore.frostJournalView)
        } else {
            frostJournalDataStore.frostJournalViList.forEach { webView ->
                webView.frostJournalSetFileChooserHandler { callback ->
                    frostJournalHandleFileChooser(callback)
                }
            }
            frostJournalDataStore.frostJournalView = frostJournalDataStore.frostJournalViList.last()

            frostJournalAttachWebViewToContainer(frostJournalDataStore.frostJournalView)
        }
        Log.d(FrostJournalApplication.FROST_JOURNAL_MAIN_TAG, "WebView list size = ${frostJournalDataStore.frostJournalViList.size}")
    }

    private fun frostJournalHandleFileChooser(callback: ValueCallback<Array<Uri>>?) {
        Log.d(FrostJournalApplication.FROST_JOURNAL_MAIN_TAG, "handleFileChooser called, callback: ${callback != null}")

        frostJournalFilePathFromChrome = callback

        val listItems: Array<out String> = arrayOf("Select from file", "To make a photo")
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> {
                    Log.d(FrostJournalApplication.FROST_JOURNAL_MAIN_TAG, "Launching file picker")
                    frostJournalTakeFile.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                1 -> {
                    Log.d(FrostJournalApplication.FROST_JOURNAL_MAIN_TAG, "Launching camera")
                    frostJournalPhoto = frostJournalViFun.frostJournalSavePhoto()
                    frostJournalTakePhoto.launch(frostJournalPhoto)
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose a method")
            .setItems(listItems, listener)
            .setCancelable(true)
            .setOnCancelListener {
                Log.d(FrostJournalApplication.FROST_JOURNAL_MAIN_TAG, "File chooser canceled")
                callback?.onReceiveValue(null)
                frostJournalFilePathFromChrome = null
            }
            .create()
            .show()
    }

    private fun frostJournalAttachWebViewToContainer(w: FrostJournalVi) {
        frostJournalDataStore.frostJournalContainerView.post {
            (w.parent as? ViewGroup)?.removeView(w)
            frostJournalDataStore.frostJournalContainerView.removeAllViews()
            frostJournalDataStore.frostJournalContainerView.addView(w)
        }
    }


}