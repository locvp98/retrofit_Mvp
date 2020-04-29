package com.sound.fbdownloder.entities

import java.io.File
import java.io.Serializable

data class VideoStory(
    val date: Long,
    var path: String,
    var nameVideo: String,
    val duration: Int,
    val file: File
) : Serializable, Comparable<VideoStory> {
    override fun compareTo(other: VideoStory): Int {
        return other.date.compareTo(date)
    }
}