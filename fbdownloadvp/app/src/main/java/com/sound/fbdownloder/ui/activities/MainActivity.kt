package com.sound.fbdownloder.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import com.google.android.material.tabs.TabLayout
import com.sound.fbdownloder.R
import com.sound.fbdownloder.adapter.ViewPageAdapter
import com.sound.fbdownloder.event.LoaddingDownloadEvent
import com.sound.fbdownloder.event.LoadingSuccessEvent
import com.sound.fbdownloder.ui.frags.DownloadFrag
import com.sound.fbdownloder.ui.frags.HistoryFrag
import com.sound.fbdownloder.ultti.Constant
import com.sound.fbdownloder.ultti.Utils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_permision.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

@SuppressLint("Registered")
class MainActivity : AppCompatActivity() {
    private var checkPre = 0
    private var isDenyShowAgain = false
    private var mLastClickTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addFragment()
        permission()
        listener()
    }

    override fun onRestart() {
        super.onRestart()
        permission()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    private fun addFragment() {
        vpDownloader.offscreenPageLimit = 1
        val myViewPageStateAdapter = ViewPageAdapter(supportFragmentManager)
        myViewPageStateAdapter.addFragment(DownloadFrag(), getString(R.string.download))
        myViewPageStateAdapter.addFragment(HistoryFrag(), getString(R.string.history))
        vpDownloader.adapter = myViewPageStateAdapter
        tabMain.setupWithViewPager(vpDownloader, true)
    }

    @Subscribe
    fun loadingRequest(event: LoaddingDownloadEvent) {
        rlView.visibility = View.VISIBLE
    }

    @Subscribe
    fun loadingRequestSuccess(event: LoadingSuccessEvent) {
        rlView.visibility = View.GONE
    }

    private fun listener() {
        btnCheckPer.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime >= 500) {
                mLastClickTime = SystemClock.elapsedRealtime()
                if (isDenyShowAgain) {
                    intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivityForResult(intent, Constant.PERMISSION)
                } else {
                    showPermission()
                }
            }
        }

        imgLogo.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }
    }

    private fun permission() {
        checkPre += 1
        if (isGranted()) {
            icPer.visibility = View.GONE
        } else {
            icPer.visibility = View.VISIBLE
            if (checkPre == 1) {
                showPermission()
            }
        }
    }

    private fun isGranted(): Boolean {
        return Utils.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun showPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            Constant.REQUEST_CODE_ASK_PERMISSIONS
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Constant.REQUEST_CODE_ASK_PERMISSIONS ->
                for (i in permissions.indices) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        permission()
                    }
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(permissions[i])) {
                                isDenyShowAgain = false
                                btnCheckPer.setText(R.string.Allow)

                            } else {
                                isDenyShowAgain = true
                                btnCheckPer.setText(R.string.goToSetting)
                            }
                        }
                    }
                }
        }
    }
}
