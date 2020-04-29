package com.sound.fbdownloder.manager

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.sound.fbdownloder.event.ProgressDownEvent
import com.sound.fbdownloder.ui.dialog.ProgressDialog
import com.sound.fbdownloder.ultti.Constant
import org.greenrobot.eventbus.EventBus
import java.io.File

class DownloadVideo(private val context: Context) {

    fun startDownload(downloadPath: String?, type: String, resulution: String) {
        val nameVideo = resulution + "_" + System.currentTimeMillis()
        try {
            val uri = Uri.parse(downloadPath!!.trim())
            val request = DownloadManager.Request(uri)

            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setTitle(nameVideo+ type)
            request.setVisibleInDownloadsUi(true)
            request.setDestinationInExternalPublicDir(
                Constant.FOLDER_FB,
                File.separator + nameVideo + type
            )
            val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downLoadId = manager.enqueue(request)
            showProgress(downLoadId, manager)
            ProgressDialog(context).dialogProgress(downLoadId,nameVideo).show()
        } catch (e: Exception) {
            Log.d("DownloaderDialog", "$e")
            e.printStackTrace()
        }
    }

    private fun showProgress(
        downLoadId: Long,
        manager: DownloadManager
    ) {
        var downloading = true
        Thread(Runnable {
            while (downloading) {
                try {
                    val q = DownloadManager.Query()
                    q.setFilterById(downLoadId)
                    val cursor = manager.query(q)
                    if (cursor == null) {
                        (context as Activity).runOnUiThread {
                            Toast.makeText(context, "", Toast.LENGTH_SHORT).show()
                        }
                        downloading = false
                        break
                    }
                    cursor.moveToFirst()
                    val bytesDownloaded =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                    val bytesTotal =
                        cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false
                    }

                    if (bytesTotal > 0) {
                        val progress = (bytesDownloaded * 100L / bytesTotal)
                        EventBus.getDefault().post(ProgressDownEvent(progress,downLoadId))
                    }
                    Thread.sleep(1000L)
                } catch (ex: Exception) {
                    Log.d("TAG_MESS", "${ex.message}")
                }
            }
        }).start()
    }
}