package com.sound.fbdownloder.ui.activities

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.SeekBar
import com.sound.fbdownloder.R
import com.sound.fbdownloder.entities.VideoStory
import com.sound.fbdownloder.ui.dialog.DeleteDialog
import com.sound.fbdownloder.ui.dialog.DetailVideoDiaLog
import com.sound.fbdownloder.ultti.Constant
import com.sound.fbdownloder.ultti.Utils
import kotlinx.android.synthetic.main.activity_play_video.*

class PlayVideoActivity : AppCompatActivity() {
    private var listVideo: ArrayList<VideoStory>? = null
    private var mLastClickTime: Long = 0
    private var position = -1
    private var handler = Handler()
    private var currentPosition = 0
    private var time = 0
    private lateinit var mRunnable: Runnable
    private var timePaste: CountDownTimer? = null
    var isTrackTouch = false
    private var timStop: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        statusBarProcess()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_video)
        getDataVideo()
        playVideo()
        listener()
        onTouchOne(rlViewPlay)
        hideTap()
    }

    private fun playVideo() {
        playVideoLocal()
        playVideoOnline()
    }

    private fun playVideoOnline() {

    }

    private fun listener() {
        imgBack.setOnClickListener {
            onBackPressed()
        }

        imvSharePlay.setOnClickListener {
            Utils.shareVideo(this, listVideo?.get(position)!!.nameVideo)
        }

        imbDetail.setOnClickListener {
            DetailVideoDiaLog(this).container(listVideo!!, position).show()
        }

        imvDeletePlay.setOnClickListener {
            DeleteDialog(this).container(position, listVideo!![position].path).show()
        }

        btnPlay.setOnTouchListener(object : View.OnTouchListener {
            @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
            override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
                if (event!!.action == MotionEvent.ACTION_DOWN) {
                    if (timePaste != null) {
                        timePaste!!.cancel()
                    }
                    vdPlay.pause()
                    stopVisible()
                    return true
                }
                if (event.action == MotionEvent.ACTION_UP) {
                    hideTap()
                    return true
                }
                return false
            }
        })

        nextVideo()
        preVideo()

    }

    private fun preVideo() {
        imbPrer.setOnTouchListener(object : View.OnTouchListener {
            @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
            override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
                btnPlay.visibility = View.VISIBLE
                btnStop.visibility = View.GONE
                if (event!!.action == MotionEvent.ACTION_DOWN) {
                    if (timePaste != null) {
                        timePaste!!.cancel()
                    }
                    if (position > 0) {
                        position -= 1
                        playVideoLocal()
                    }
                    return true
                }
                if (event.action == MotionEvent.ACTION_UP) {
                    hideTap()
                    return true
                }
                return false
            }
        })
    }

    private fun nextVideo() {
        imbNext.setOnTouchListener(object : View.OnTouchListener {
            @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
            override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
                btnPlay.visibility = View.VISIBLE
                btnStop.visibility = View.GONE
                if (event!!.action == MotionEvent.ACTION_DOWN) {
                    if (timePaste != null) {
                        timePaste!!.cancel()
                    }

                    if (position < (listVideo!!.size - 1)) {
                        position += 1
                        playVideoLocal()
                    }
                    return true
                }
                if (event.action == MotionEvent.ACTION_UP) {
                    hideTap()
                    return true
                }

                return false
            }
        })
    }

    private fun getDataVideo() {
        listVideo = ArrayList()
        val args = intent.getBundleExtra(Constant.BUNDLE)
        position = intent.getIntExtra(Constant.POSITION, 0)
        listVideo!!.addAll((args!!.getSerializable(Constant.ARRAYLIST) as ArrayList<VideoStory>?)!!)
    }

    private fun playVideoLocal() {
        runVideo(listVideo?.get(position)!!.nameVideo)
        runVideo(listVideo?.get(position)!!.duration)
    }

    private fun runVideo(name: String) {
        txtTitles.text = name
        val path = Utils.getFileVideo(name)
        val uri = Uri.parse(path.toString())
        vdPlay.requestFocus()
        vdPlay.setVideoURI(uri)
        vdPlay.start()
    }

    @SuppressLint("SetTextI18n")
    private fun runVideo(durations: Int) {
        mRunnable = Runnable {
            val duration = this.vdPlay.duration
            updateProgress(duration)
            txtTimes.text = "-" + Utils.formatTime(time)
            handler.postDelayed(mRunnable, 1)
        }
        handler.postDelayed(
            mRunnable,
            1000
        )
        updateSeekBar(durations)
    }

    private fun updateSeekBar(durations: Int) {
        sbVideo.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var progressValues = 0

            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                progressValues = p1
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                isTrackTouch = true
                if (timePaste != null) {
                    timePaste!!.cancel()
                }
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                hideTap()
                isTrackTouch = false
                val valueSeekTo = (progressValues * durations) / p0!!.max
                vdPlay.seekTo(valueSeekTo)
            }
        })

        btnStop.setOnClickListener {
            playVisible()
            vdPlay.start()
            vdPlay.start()
        }
    }

    private fun updateProgress(duration: Int) {
        currentPosition = vdPlay.currentPosition
        if (duration != 0) {
            val progress = currentPosition * 1000 / duration
            val times = (currentPosition / 1000)
            time = duration / 1000 - times
            if (!isTrackTouch) {
                sbVideo.progress = progress
            }
        }
    }

    private fun stopVisible() {
        btnPlay.visibility = View.GONE
        btnStop.visibility = View.VISIBLE
        imbNext.visibility = View.VISIBLE
        imbPrer.visibility = View.VISIBLE
    }

    private fun playVisible() {
        btnPlay.visibility = View.VISIBLE
        btnStop.visibility = View.GONE
        imbNext.visibility = View.VISIBLE
        imbPrer.visibility = View.VISIBLE
    }

    private fun hideTap() {
        timePaste = object : CountDownTimer(4000, 4000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                Utils.setAnimationHide(rlTime)
                rlTime.visibility = View.GONE
            }
        }
        timePaste!!.start()
    }


    private fun showHideTouch() {
        if (rlTime.visibility == View.GONE) {
            hideTap()
            Utils.setAnimationShow(rlTime)
            rlTime.visibility = View.VISIBLE
        } else {
            Utils.setAnimationHide(rlTime)
            rlTime.visibility = View.GONE
        }
    }

    private fun onTap() {
        if (timePaste != null) {
            timePaste!!.cancel()
        }
        showHideTouch()
    }

    private fun onTouchOne(view: View) {
        view.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime >= 500) {
                mLastClickTime = SystemClock.elapsedRealtime()
                onTap()
            }
        }
    }

    override fun onPause() {
        vdPlay.pause()
        timStop = vdPlay.currentPosition
        checkMaxVideo()
        super.onPause()
    }

    private fun checkMaxVideo() {
        playVisible()
    }

    override fun onResume() {
        super.onResume()
        vdPlay.seekTo(timStop)
        vdPlay.start()
    }

    private fun statusBarProcess() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }
}
