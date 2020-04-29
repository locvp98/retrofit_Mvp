package com.sound.fbdownloder.task

import android.content.Context
import android.os.AsyncTask
import com.sound.fbdownloder.entities.InfoLink
import com.sound.fbdownloder.ultti.Constant
import org.jsoup.Jsoup
import org.jsoup.select.Elements


class RequestVideo(private val context: Context, private val callback: CallBack) :
    AsyncTask<String, Void, ArrayList<InfoLink>>() {

    override fun onPreExecute() {
        super.onPreExecute()
        callback.onPre()
    }

    override fun doInBackground(vararg params: String?): ArrayList<InfoLink>? {
        return params[0]?.let { responseLink(it) }
    }

    override fun onPostExecute(result: ArrayList<InfoLink>?) {
        super.onPostExecute(result)
        callback.onSuccess(result!!)
    }

    private fun responseLink(url: String): ArrayList<InfoLink> {
        val listInfo = ArrayList<InfoLink>()
        try {
            val document = Jsoup.connect(Constant.BASE_URL + url).get()
            val div = document.select("div.col-md-6")
            val meta = div.select("div.meta")
            val ul = meta.select("ul[class=remove-style download-options]")

            for (i in 0 until ul.size) {
                val li = meta.select("li")
                val a: Elements = li.select("a")
                val download = a.attr("href")
                if (li.toString().contains(Constant.SD)){
                    val resolution = Constant.SD
                    listInfo.add(InfoLink(resolution, download))
                }

                if (li.toString().contains(Constant.HD)){
                    val resolution = Constant.HD
                    listInfo.add(InfoLink(resolution, download))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return listInfo
    }

    interface CallBack {
        fun onPre()
        fun onSuccess(listInfo: ArrayList<InfoLink>)
        fun onError()
    }


}
