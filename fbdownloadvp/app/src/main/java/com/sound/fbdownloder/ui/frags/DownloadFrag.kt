package com.sound.fbdownloder.ui.frags

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.sound.fbdownloder.R
import com.sound.fbdownloder.entities.InfoLink
import com.sound.fbdownloder.event.LoaddingDownloadEvent
import com.sound.fbdownloder.event.LoadingSuccessEvent
import com.sound.fbdownloder.task.RequestVideo
import com.sound.fbdownloder.ui.activities.BrowseActivity
import com.sound.fbdownloder.ui.dialog.CheckRequestDialog
import com.sound.fbdownloder.ui.dialog.DownloaderDialog
import com.sound.fbdownloder.ultti.Constant
import kotlinx.android.synthetic.main.fragment_downloader.*
import org.greenrobot.eventbus.EventBus

class DownloadFrag : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context!!)
            .inflate(R.layout.fragment_downloader, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listener()
    }

    override fun onResume() {
        super.onResume()
        pasteText()
    }

    private fun listener() {
        btnBrowsefb?.setOnClickListener {
            startActivity(Intent(activity!!, BrowseActivity::class.java))
        }

        txtPaste.setOnClickListener {
            pasteText()
        }

        btnDownload.setOnClickListener {
            if (isDownload()) {
                downloadedRequest()
            } else {
                CheckRequestDialog(context!!).requestDialog(Constant.KEY_INTERNET).show()
            }
        }
    }

    private fun downloadedRequest() {
        RequestVideo(context!!, object : RequestVideo.CallBack {
            override fun onPre() {
                EventBus.getDefault().post(LoaddingDownloadEvent())
            }

            override fun onSuccess(listInfo: ArrayList<InfoLink>) {
                EventBus.getDefault().post(LoadingSuccessEvent())
                DownloaderDialog(context!!).requestDownload(listInfo, Constant.KEY_DOWNLOAD_PASTE)
                    .show()
            }

            override fun onError() {
            }
        }).execute(edtUrls.text.toString())
    }

    private fun pasteText() {
        edtUrls?.setText("")
        val clipboard = context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val clip = clipboard!!.primaryClip
        if (clip != null && clip.itemCount > 0) {
            val clipboardText = clipboard.text.toString()
            if (!clipboardText.startsWith(Constant.PORT1) && !clipboardText.startsWith(Constant.PORT2) && !clipboardText.startsWith(
                    Constant.PORT3
                )
            ) {
                Toast.makeText(context!!, context!!.getString(R.string.noLinkFb), Toast.LENGTH_LONG)
                    .show()
            }

            if (clipboardText.startsWith(Constant.PORT1) || clipboardText.startsWith(Constant.PORT2) || clipboardText.startsWith(
                    Constant.PORT3
                )
            ) {
                edtUrls?.setText(clipboardText)
            }
        } else {
            edtUrls?.setText("")
        }
    }

    private fun isDownload(): Boolean {
        if (isConnection()) {
            if (isLinkSatisfy()) {
                return true
            } else {
                CheckRequestDialog(context!!).requestDialog(Constant.KEY_INTERNET).show()
            }
        }
        return false
    }

    private fun isConnection(): Boolean {
        try {
            val connectivity =
                context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = connectivity.allNetworkInfo
            for (i in info) if (i.state == NetworkInfo.State.CONNECTED) {
                return true
            }
        } catch (e: Exception) {
            print(e)
        }
        return false
    }

    private fun isLinkSatisfy(): Boolean {
        if (edtUrls?.text.toString().startsWith(Constant.PORT1) || edtUrls?.text.toString().startsWith(
                Constant.PORT2
            ) || edtUrls?.text.toString().startsWith(
                Constant.PORT3
            )
        ) {
            return true
        }
        return false
    }
}
