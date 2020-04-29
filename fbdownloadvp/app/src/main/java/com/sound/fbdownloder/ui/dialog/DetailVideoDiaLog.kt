package com.sound.fbdownloder.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.sound.fbdownloder.R
import com.sound.fbdownloder.entities.VideoStory
import com.sound.fbdownloder.ultti.Utils
import kotlinx.android.synthetic.main.dialog_detail_video.*
import java.util.*
import kotlin.collections.ArrayList

class DetailVideoDiaLog(context: Context) : Dialog(context) {
    var listDetail: ArrayList<VideoStory>? = null

    init {
        this.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        context.setTheme(R.style.ThemeDialog)
        setContentView(R.layout.dialog_detail_video)
        listDetail = ArrayList()
    }

    fun container(listDetailVideo: ArrayList<VideoStory>, position: Int): DetailVideoDiaLog {
            val detailVideo = listDetailVideo[position]
                txtDate.text = Utils.sdf().format(Date(detailVideo.date)).toString()
                txtPath.text = detailVideo.path
                txtType.text = "VIDEO/mp4"
                txtName.text = detailVideo.nameVideo
        return this
    }
}