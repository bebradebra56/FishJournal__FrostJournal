package com.fishjorunal.sofircl

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.fishjorunal.sofircl.lpnyht.FrostJournalGlobalLayoutUtil
import com.fishjorunal.sofircl.lpnyht.frostJournalSetupSystemBars
import com.fishjorunal.sofircl.lpnyht.presentation.app.FrostJournalApplication
import com.fishjorunal.sofircl.lpnyht.presentation.pushhandler.FrostJournalPushHandler
import org.koin.android.ext.android.inject

class FrostJournalActivity : AppCompatActivity() {

    private val frostJournalPushHandler by inject<FrostJournalPushHandler>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        frostJournalSetupSystemBars()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_frost_journal)

        val frostJournalRootView = findViewById<View>(android.R.id.content)
        FrostJournalGlobalLayoutUtil().frostJournalAssistActivity(this)
        ViewCompat.setOnApplyWindowInsetsListener(frostJournalRootView) { frostJournalView, frostJournalInsets ->
            val frostJournalSystemBars = frostJournalInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val frostJournalDisplayCutout = frostJournalInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val frostJournalIme = frostJournalInsets.getInsets(WindowInsetsCompat.Type.ime())


            val frostJournalTopPadding = maxOf(frostJournalSystemBars.top, frostJournalDisplayCutout.top)
            val frostJournalLeftPadding = maxOf(frostJournalSystemBars.left, frostJournalDisplayCutout.left)
            val frostJournalRightPadding = maxOf(frostJournalSystemBars.right, frostJournalDisplayCutout.right)
            window.setSoftInputMode(FrostJournalApplication.frostJournalInputMode)

            if (window.attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) {
                Log.d(FrostJournalApplication.FROST_JOURNAL_MAIN_TAG, "ADJUST PUN")
                val frostJournalBottomInset = maxOf(frostJournalSystemBars.bottom, frostJournalDisplayCutout.bottom)

                frostJournalView.setPadding(frostJournalLeftPadding, frostJournalTopPadding, frostJournalRightPadding, 0)

                frostJournalView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = frostJournalBottomInset
                }
            } else {
                Log.d(FrostJournalApplication.FROST_JOURNAL_MAIN_TAG, "ADJUST RESIZE")

                val frostJournalBottomInset = maxOf(frostJournalSystemBars.bottom, frostJournalDisplayCutout.bottom, frostJournalIme.bottom)

                frostJournalView.setPadding(frostJournalLeftPadding, frostJournalTopPadding, frostJournalRightPadding, 0)

                frostJournalView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = frostJournalBottomInset
                }
            }



            WindowInsetsCompat.CONSUMED
        }
        Log.d(FrostJournalApplication.FROST_JOURNAL_MAIN_TAG, "Activity onCreate()")
        frostJournalPushHandler.frostJournalHandlePush(intent.extras)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            frostJournalSetupSystemBars()
        }
    }

    override fun onResume() {
        super.onResume()
        frostJournalSetupSystemBars()
    }
}