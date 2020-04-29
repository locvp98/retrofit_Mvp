package com.sound.fbdownloder.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.sound.fbdownloder.entities.VideoStory
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sound.fbdownloder.R
import com.sound.fbdownloder.ui.activities.PlayVideoActivity
import com.sound.fbdownloder.ultti.Constant
import com.sound.fbdownloder.ultti.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_meta.*

class HistoryAdapter(private var listMeta: ArrayList<VideoStory>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            Constant.ITEM_DATA -> {
                return ItemViewAdapter(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_meta,
                        parent,
                        false
                    )
                )
            }

            else -> {
                return ItemAds(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_meta, parent, false
                    )
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return listMeta.size
    }

    override fun getItemViewType(position: Int): Int {
        return Constant.ITEM_DATA
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemViewAdapter -> {
                holder.imageContainer(listMeta, position)
            }

            is ItemAds -> {
                holder.adsContainer()
            }
        }
    }

    class ItemViewAdapter(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {
        override val containerView: View?
            get() = itemView

        fun imageContainer(
            listMeta: ArrayList<VideoStory>,
            position: Int) {
            val item = listMeta[position]
            Glide.with(itemView.context).load(item.file.path).into(imgVideoWhat)
            txtTime.text = Utils.conVerDurationToTime(item.duration)
            listener(listMeta,position)
        }

       private fun listener(listMeta: ArrayList<VideoStory>, position: Int) {
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, PlayVideoActivity::class.java)
                val args = Bundle()
                intent.putExtra(Constant.BUNDLE, args)
                args.putSerializable(Constant.ARRAYLIST, listMeta)
                intent.putExtra(Constant.POSITION, position)
                itemView.context.startActivity(intent)
            }
        }
    }

    class ItemAds(itemView: View) : RecyclerView.ViewHolder(itemView), LayoutContainer {
        override val containerView: View?
            get() = itemView

        fun adsContainer() {

        }
    }
}