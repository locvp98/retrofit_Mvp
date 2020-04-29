package com.sound.fbdownloder.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.bumptech.glide.Glide
import com.sound.fbdownloder.R
import com.sound.fbdownloder.ultti.Constant
import kotlinx.android.synthetic.main.dialog_nointernet.*

class CheckRequestDialog(context: Context) : Dialog(context) {

    init {
        this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        context.setTheme(R.style.ThemeDialog)
        setContentView(R.layout.dialog_nointernet)
    }

    fun requestDialog(key:String): CheckRequestDialog {
        showDialog(key)
        return this
    }

    private fun showDialog(key: String) {
        when(key){
            Constant.KEY_INTERNET->{
                setIconLayout(R.drawable.img_error,context.getString(R.string.no_Internet),context.getString(R.string.title_Internet))
            }

            Constant.KEY_LINKPRIVATE->{
                setIconLayout(R.drawable.img_error,context.getString(R.string.no_Internet),context.getString(R.string.title_Internet))
            }

            Constant.KEY_NOTFOUND->{
                setIconLayout(R.drawable.img_error,context.getString(R.string.no_Internet),context.getString(R.string.title_Internet))
            }
        }
    }

    private fun setIconLayout(iconId:Int, title:String, dec:String){
        Glide.with(context).load(iconId).into(imgErr)
        txtTitle.text = title
        txtDec.text = dec
    }
}