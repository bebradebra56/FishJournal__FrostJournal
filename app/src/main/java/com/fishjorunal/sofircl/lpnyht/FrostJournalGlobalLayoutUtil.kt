package com.fishjorunal.sofircl.lpnyht

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import com.fishjorunal.sofircl.lpnyht.presentation.app.FrostJournalApplication

class FrostJournalGlobalLayoutUtil {

    private var frostJournalMChildOfContent: View? = null
    private var frostJournalUsableHeightPrevious = 0

    fun frostJournalAssistActivity(activity: Activity) {
        val content = activity.findViewById<FrameLayout>(android.R.id.content)
        frostJournalMChildOfContent = content.getChildAt(0)

        frostJournalMChildOfContent?.viewTreeObserver?.addOnGlobalLayoutListener {
            possiblyResizeChildOfContent(activity)
        }
    }

    private fun possiblyResizeChildOfContent(activity: Activity) {
        val frostJournalUsableHeightNow = frostJournalComputeUsableHeight()
        if (frostJournalUsableHeightNow != frostJournalUsableHeightPrevious) {
            val frostJournalUsableHeightSansKeyboard = frostJournalMChildOfContent?.rootView?.height ?: 0
            val frostJournalHeightDifference = frostJournalUsableHeightSansKeyboard - frostJournalUsableHeightNow

            if (frostJournalHeightDifference > (frostJournalUsableHeightSansKeyboard / 4)) {
                activity.window.setSoftInputMode(FrostJournalApplication.frostJournalInputMode)
            } else {
                activity.window.setSoftInputMode(FrostJournalApplication.frostJournalInputMode)
            }
//            mChildOfContent?.requestLayout()
            frostJournalUsableHeightPrevious = frostJournalUsableHeightNow
        }
    }

    private fun frostJournalComputeUsableHeight(): Int {
        val r = Rect()
        frostJournalMChildOfContent?.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top  // Visible height без status bar
    }
}