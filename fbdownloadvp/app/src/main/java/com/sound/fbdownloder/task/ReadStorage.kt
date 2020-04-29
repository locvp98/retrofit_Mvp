package com.sound.fbdownloder.task

import com.sound.fbdownloder.entities.VideoStory
import android.media.MediaMetadataRetriever
import android.os.AsyncTask
import android.util.Log
import com.orhanobut.hawk.Hawk
import com.sound.fbdownloder.ultti.Constant
import com.sound.fbdownloder.ultti.Utils
import java.io.File

class ReadStorage(private val mCallbackVideo: VideoCallback) :
    AsyncTask<Void, Void, ArrayList<VideoStory>>() {

    private val videoWhats = ArrayList<VideoStory>()

    override fun onPreExecute() {
        super.onPreExecute()
        mCallbackVideo.onPreVideo()
    }

    override fun doInBackground(vararg params: Void?): ArrayList<VideoStory> {
        return readVideoWhat()
    }

    override fun onPostExecute(result: ArrayList<VideoStory>?) {
        super.onPostExecute(result)
        result?.let {
            mCallbackVideo.onVideoSuccess(result)
        }
    }

    private fun readVideoWhat(): ArrayList<VideoStory> {
        try {
            val directory = File(Utils.getFolderPathWhat())

            if (!directory.exists()) {
                return videoWhats
            }
            val files = directory.listFiles()
            if (files.isNullOrEmpty()) {
                return videoWhats
            }

            val retriever = MediaMetadataRetriever()
            Log.d("writeInternal", "1 ${Utils.getFolderPathWhat()}")

            val listDetail =
                Hawk.get<ArrayList<VideoStory>>(Constant.WHAT_APP_INTERNAL) ?: ArrayList()
            Log.d("writeInternal", "2 ${Utils.getFolderPathWhat()}")

            for (i in files.indices) {
                val file = files[i]
                val nameVideo = file.name

                if (isFileRead(nameVideo, listDetail)) {
                    continue
                }
                readMedia(retriever, nameVideo, file, listDetail)
            }
            updateInternal(files, listDetail)
        } catch (e: Exception) {
            print(e)
        }

        videoWhats.sort()
        return videoWhats
    }

    private fun updateInternal(files: Array<File>?, listDetail: ArrayList<VideoStory>) {
        for (i in files!!.indices) {
            val detail = detailVideoFromHawk(files[i].name, listDetail)
            if (detail != null) {
                videoWhats.add(detail)
            }
        }
    }

    private fun readMedia(
        retriever: MediaMetadataRetriever,
        nameVideo: String,
        file: File,
        listDetail: ArrayList<VideoStory>
    ) {
        try {
            val filePath = Utils.getFolderPathWhat() + File.separator + nameVideo
            retriever.setDataSource(filePath)
            val date = file.lastModified()
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)

            writeInternal(
                date,
                filePath,
                nameVideo,
                listDetail,
                duration,
                file
            )

        } catch (e: Exception) {

            e.printStackTrace()
        }
    }

    private fun writeInternal(
        date: Long,
        filePath: String,
        nameVideo: String,
        listDetail: ArrayList<VideoStory>,
        duration: String,
        file: File
    ) {
        listDetail.add(
            VideoStory(
                date,
                filePath,
                nameVideo,
                duration.toInt(),
                file
            )
        )
        Hawk.put(Constant.WHAT_APP_INTERNAL, listDetail)
    }

    private fun detailVideoFromHawk(name: String, list: ArrayList<VideoStory>): VideoStory? {
        for (i in list.indices) {
            if (list[i].nameVideo == name) {
                return list[i]
            }
        }
        return null
    }

    private fun isFileRead(name: String, detailVideo: ArrayList<VideoStory>): Boolean {
        for (i in detailVideo.indices) {
            if (detailVideo[i].nameVideo == name) {
                return true
            }
        }
        return false
    }

    interface VideoCallback {
        fun onVideoSuccess(listWhat: ArrayList<VideoStory>)
        fun onPreVideo()
    }
}