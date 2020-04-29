package com.sound.fbdownloder.ui.activities

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import com.sound.fbdownloder.R
import com.sound.fbdownloder.ui.dialog.DownloaderDialog
import com.sound.fbdownloder.ultti.Constant
import kotlinx.android.synthetic.main.activity_browse.*

class BrowseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse)
        webViewClient()
        listener()
    }

    private fun listener() {
        img_refresh.setOnClickListener {
            webFacebook.reload()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun webViewClient() {
        val intent = intent
        val links = intent.getStringExtra(Constant.LINK_VIDEO)
        webFacebook.isVerticalScrollBarEnabled = false
        webFacebook.isHorizontalScrollBarEnabled = false
        webFacebook.settings.javaScriptEnabled = true
        webFacebook.settings.pluginState = WebSettings.PluginState.ON
        webFacebook.settings.builtInZoomControls = true
        webFacebook.settings.displayZoomControls = true
        webFacebook.settings.useWideViewPort = true
        webFacebook.settings.loadWithOverviewMode = true
        webFacebook.settings.userAgentString =
            "Mozilla/5.0 (Linux; U; Android 4.1.1; en-gb; Build/KLP) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Safari/534.30"
        webFacebook.addJavascriptInterface(this, "FBDownloader")
        webFacebook.webViewClient = (object : WebViewClient() {
            override fun onLoadResource(view: WebView, url: String) {
                txtLinkWeb.text = view.originalUrl

                if (view.originalUrl != null) {
                    saveUrl(view.originalUrl)
                }

                webFacebook.loadUrl(
                    "javascript:(function prepareVideo() { "
                            + "var el = document.querySelectorAll('div[data-sigil]');"
                            + "for(var i=0;i<el.length; i++)"
                            + "{"
                            + "var sigil = el[i].dataset.sigil;"
                            + "if(sigil.indexOf('inlineVideo') > -1){"
                            + "delete el[i].dataset.sigil;"
                            + "var jsonData = JSON.parse(el[i].dataset.store);"
                            + "console.log(el[i].dataset.store);"
                            + "el[i].setAttribute('onClick', 'FBDownloader.processVideo(\"'+jsonData['src']+'\",\"'+jsonData['videoID']+'\");');"
                            + "}" + "}" + "})()"
                )
                webFacebook.loadUrl(("javascript:( window.onload=prepareVideo" + ")()"))
            }

            override fun onPageFinished(view: WebView, url: String) {
            }

            fun saveUrl(url: String) {
                val pref = applicationContext.getSharedPreferences(
                    Constant.ADDRESS_BROWSE,
                    Context.MODE_PRIVATE
                )
                val editor = pref.edit()
                editor.putString(Constant.ADDRESS_URL, url)
                editor.apply()
            }
        })

        val pref =
            applicationContext.getSharedPreferences(Constant.ADDRESS_BROWSE, Context.MODE_PRIVATE)
        val linkLoad = pref.getString(Constant.ADDRESS_URL, "")

        CookieSyncManager.createInstance(this)
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        CookieSyncManager.getInstance().startSync()

        if (links.isNullOrEmpty()) {
            if (linkLoad.isNullOrEmpty()) {
                webFacebook.loadUrl(Constant.PORT1)
            } else {
                webFacebook.loadUrl(linkLoad)
            }
        } else {
            webFacebook.loadUrl(links)
        }
    }

    @JavascriptInterface
    fun processVideo(vidData: String, vidID: String) {
        val url = Constant.PORT1 + vidID
        this.runOnUiThread {
            DownloaderDialog(this).requestBrowse(vidData,url).show()
        }
    }

    override fun onBackPressed() {
        if (webFacebook.canGoBack()) {
            webFacebook.goBack()
            return
        }else{
            finish()
        }
    }
}
