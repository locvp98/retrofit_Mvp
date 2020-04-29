package com.sound.fbdownloder.ultti

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.view.View
import android.view.animation.AlphaAnimation
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.sound.fbdownloder.BuildConfig
import java.io.File
import java.text.SimpleDateFormat

object Utils {
    fun checkPermission(context: Context, permission: String): Boolean {
        return if (isMarshmallow()) {
            val result = ContextCompat.checkSelfPermission(context, permission)
            result == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun isMarshmallow(): Boolean {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1
    }

    fun getFolderPathWhat(): String {
        return Environment.getExternalStorageDirectory().toString() + File.separator + Constant.FOLDER_FB
    }

    fun conVerDurationToTime(milliseconds: Int): String {
        var finalTimerString = ""
        val hours = (milliseconds / (1000 * 60 * 60))
        val minutes = (milliseconds % (1000 * 60 * 60)) / (1000 * 60)
        val seconds = ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000)
        if (hours > 0) {
            finalTimerString = "$hours :"
        }

        val secondsString = if (seconds < 10) {
            "0$seconds"
        } else {
            "" + seconds
        }

        return "$finalTimerString$minutes:$secondsString"
    }

    fun getFileVideo(name: String): File {
        return Environment.getExternalStoragePublicDirectory(Constant.FOLDER_FB + "/" + name)
    }


    @SuppressLint("DefaultLocale")
    fun formatTime(values: Int): String {
        val hours = values / 3600
        val minutes = values / 60 - hours * 60
        val seconds = values - hours * 3600 - minutes * 60
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }


    fun shareVideo(context: Context, fileName: String) {
        try {
            val path =
                Environment.getExternalStorageDirectory().toString() + File.separator + Constant.FOLDER_FB + "/" + fileName
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "video/mp4"
            val fileToShare = File(path)
            val uri = FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".FileProvider",
                fileToShare
            )

            sharingIntent.putExtra(Intent.EXTRA_STREAM, uri)
            context.startActivity(Intent.createChooser(sharingIntent, "Share..."))
        } catch (ex: Exception) {

            ex.printStackTrace()
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun sdf(): SimpleDateFormat {
        return SimpleDateFormat("yyyy/MM/dd HH:mm")
    }

    fun setAnimationHide(view: View) {
        val animation = AlphaAnimation(1.0f, 0.0f)
        animation.duration = 200
        view.startAnimation(animation)
    }

    fun setAnimationShow(view: View) {
        val animation = AlphaAnimation(0.5f, 1.0f)
        animation.duration = 300
        view.startAnimation(animation)
    }



}