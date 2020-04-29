package com.sound.fbdownloder.ui.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.util.TypedValue
import android.view.Window
import android.widget.RelativeLayout
import com.sound.fbdownloder.event.ProgressDownEvent
import com.sound.fbdownloder.R
import kotlinx.android.synthetic.main.dialog_progress.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ProgressDialog(context: Context) : Dialog(context) {
    private var ids = 0L

    init {
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_progress)
        context.setTheme(R.style.ThemeDialog)
        setCanceledOnTouchOutside(false)
        EventBus.getDefault().register(this)
    }

    fun dialogProgress(idPro: Long, name: String): ProgressDialog {
        ids = idPro
        txtNameFile.text = name
        return this
    }

    @SuppressLint("SetTextI18n")
    @Subscribe
    fun progress(event: ProgressDownEvent) {
        txtTextPro.text = event.progress.toString() + "%"
        if (ids == event.id) {
            val hide = 0.94 * event.progress
            updateProgress(hide.toInt())
        }
    }

    private fun updateProgress(hide: Int) {
        var height = convertDpToPixel(94, context)
        height -= convertDpToPixel(hide, context)
        val params = RelativeLayout.LayoutParams(convertDpToPixel(94, context), height)
        Log.d("ProgressDonwEvent","$params")
        imgProgress.layoutParams = params


    }

    private fun convertDpToPixel(dp: Int, context: Context): Int {
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        )
        return Math.round(px)
    }

    override fun dismiss() {
        EventBus.getDefault().unregister(this)
        super.dismiss()
    }
}