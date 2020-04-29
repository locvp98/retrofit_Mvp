package com.sound.fbdownloder.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import com.sound.fbdownloder.R
import com.sound.fbdownloder.entities.InfoLink
import com.sound.fbdownloder.task.RequestVideo
import com.sound.fbdownloder.ultti.Constant
import com.sound.fbdownloder.manager.DownloadVideo
import kotlinx.android.synthetic.main.dialog_resolution.*

class DownloaderDialog(context: Context) : Dialog(context) {

    private var linkHD = ""
    private var linkSD = ""
    private var linkAudio = ""

    init {
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_resolution)
        context.setTheme(R.style.ThemeDialog)
        setCanceledOnTouchOutside(false)
    }

    fun requestDownload(listLink: ArrayList<InfoLink>, key: String): DownloaderDialog {
        checkKey(listLink, key)
        return this
    }

    fun requestBrowse(linkSDBrowse: String, url: String): DownloaderDialog {
        checkLinkSd(linkSDBrowse)
        listener()
        downloadBrowse(url)
        return this
    }

    private fun checkLinkSd(linkSDBrowse: String) {
        linkSD = linkSDBrowse
        linkSdBrowse()
    }

    private fun checkKey(listLink: ArrayList<InfoLink>, key: String) {
        when (key) {
            Constant.KEY_DOWNLOAD_PASTE -> {
                downLoadPaste(listLink)
            }
        }
    }

    private fun downloadBrowse(url: String) {
       requestLink(url)
    }

    private fun requestLink(url: String) {
        RequestVideo(context, object : RequestVideo.CallBack {
            override fun onPre() {
                linkHdBrowse()
            }

            override fun onSuccess(listInfo: ArrayList<InfoLink>) {
                analysisLink(listInfo)
                goneSpinKitView()
            }

            override fun onError() {
            }
        }).execute(url)
    }

    private fun downLoadPaste(listLink: ArrayList<InfoLink>) {
        analysisLink(listLink)
        goneSpinKitView()
        listener()

    }

    private fun listener() {
        btnSDDown.setOnClickListener {
            DownloadVideo(context).startDownload(linkSD, Constant.TYPE_VIDEO, Constant.SD)
        }

        btnHDDown.setOnClickListener {
            DownloadVideo(context).startDownload(linkHD, Constant.TYPE_VIDEO, Constant.HD)
        }

        txtClose.setOnClickListener {
            dismiss()
        }
    }

    private fun analysisLink(listLink: ArrayList<InfoLink>) {
        var resolution: String?
        for (i in 0 until listLink.size) {
            resolution = listLink[i].resolution
            when {
                resolution.startsWith(Constant.HD) -> {
                    linkHD = listLink[i].videoLink
                }
                resolution.startsWith(Constant.SD) -> {
                    linkSD = listLink[i].videoLink
                }
                resolution.startsWith(Constant.AUDIO) -> {
                    linkAudio = listLink[i].videoLink
                }
            }
        }
        checkEmpty()
        linkNotEmpty()
    }

    private fun checkEmpty() {
        if (linkHD.isEmpty()) {
            relativeHD.alpha = 0.3f
            btnHDDown.isEnabled = false
            btnHDDown.text = context.getString(R.string.hdVideoFound)
        }

        if (linkSD.isEmpty()) {
            rlSd.alpha = 0.3f
            btnSDDown.isEnabled = false
            btnSDDown.text = context.getString(R.string.sdVideoFound)
        }
    }

    private fun linkNotEmpty() {
        if (linkHD.isNotEmpty()) {
            relativeHD.alpha = 1f
            btnHDDown.isEnabled = true
            imgIcLoadHD.visibility = View.VISIBLE
            btnHDDown.text = context.getString(R.string.hdDownload)
        }

        if (linkSD.isNotEmpty()) {
            rlSd.alpha = 1f
            btnSDDown.isEnabled = true
            imgIcLoad.visibility = View.VISIBLE
            btnSDDown.text = context.getString(R.string.sdDownload)
        }
    }

    private fun goneSpinKitView() {
        spinKitSd.visibility = View.GONE
        spinKitHD.visibility = View.GONE
    }

    private fun linkSdBrowse(){
        rlSd.alpha = 1f
        btnSDDown.isEnabled = true
        imgIcLoad.visibility = View.VISIBLE
        spinKitSd.visibility = View.GONE
        btnSDDown.text = context.getString(R.string.sdDownload)
    }

    private fun linkHdBrowse(){
        relativeHD.alpha = 0.3f
        btnHDDown.isEnabled = false
        btnHDDown.text = context.getString(R.string.hdLoading)
    }
}