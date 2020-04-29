package com.sound.fbdownloder.ui.frags

import com.sound.fbdownloder.entities.VideoStory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.sound.fbdownloder.R
import com.sound.fbdownloder.adapter.HistoryAdapter
import com.sound.fbdownloder.event.DeleteEvent
import com.sound.fbdownloder.task.ReadStorage
import com.sound.fbdownloder.ultti.Constant
import kotlinx.android.synthetic.main.fragment_video.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class HistoryFrag : Fragment() {
    private var adapter: HistoryAdapter? = null
    private var listData: ArrayList<VideoStory>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(context!!).inflate(R.layout.fragment_video, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        readVideoStorage()
        configAdapter()
        listener()
    }

    private fun listener() {
        swRefresh?.setOnRefreshListener {
            readVideoStorage()
        }
    }

    private fun readVideoStorage() {
        ReadStorage(object : ReadStorage.VideoCallback {
            override fun onVideoSuccess(listWhat: ArrayList<VideoStory>) {
                listData!!.clear()
                swRefresh.isRefreshing = false
                if (listWhat.isEmpty()) {
                    llHowToUse.visibility = View.VISIBLE
                } else {
                    llHowToUse.visibility = View.GONE
                    readSuccess(listWhat)
                }
            }

            override fun onPreVideo() {
            }
        }).execute()
    }

    private fun configAdapter() {
        listData = ArrayList()
        adapter = HistoryAdapter(listData!!)
        val layoutManager = GridLayoutManager(context, 3)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapter?.getItemViewType(position)) {
                    Constant.ITEM_DATA -> 1
                    Constant.ITEM_ADS -> 3
                    else -> -1
                }
            }
        }
        
        rcPhoto?.layoutManager = layoutManager
        rcPhoto?.adapter = adapter
        adapter!!.notifyDataSetChanged()
    }

    private fun readSuccess(listWhat: ArrayList<VideoStory>) {
        listData!!.addAll(listWhat)
        adapter!!.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun deleteVideo(event: DeleteEvent) {
        readVideoStorage()
    }
}