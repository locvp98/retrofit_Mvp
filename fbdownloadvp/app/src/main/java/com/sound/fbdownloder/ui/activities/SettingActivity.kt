package com.sound.fbdownloder.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import com.sound.fbdownloder.R
import com.sound.fbdownloder.ultti.Constant
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {
    var mLastClickTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        listener()
    }

    private fun listener() {
        imgBackSetting.setOnClickListener {
            onBackPressed()
        }

        llShareTo.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime >= 1000) {
                mLastClickTime = SystemClock.elapsedRealtime()
                val link = Constant.GOOGLE_STORE + this.packageName
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                intent.putExtra(
                    Intent.EXTRA_SUBJECT,
                    this.applicationInfo?.loadLabel(this.packageManager)
                )
                intent.putExtra(Intent.EXTRA_TEXT, link)
                startActivity(Intent.createChooser(intent, "Share app"))
            }
        }

    }
}
