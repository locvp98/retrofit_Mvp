package com.sound.fbdownloder.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.Toast
import com.sound.fbdownloder.R
import com.sound.fbdownloder.event.DeleteEvent
import kotlinx.android.synthetic.main.dialog_delete.*
import org.greenrobot.eventbus.EventBus
import java.io.File

@Suppress("DEPRECATION")
class DeleteDialog(context: Context) : Dialog(context) {
    init {
        this.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        context.setTheme(R.style.ThemeDialog)
        context.setTheme(R.style.ThemeDialog)
        setContentView(R.layout.dialog_delete)
        txtCancel.setOnClickListener {
            dismiss()
        }
    }

    fun container(pos: Int, path: String): DeleteDialog {
        listener(path, pos)
        return this
    }

    private fun listener(path: String, position: Int) {
        txtYest.setOnClickListener {
            try {
                val file = File(path)
                file.delete()
                Toast.makeText(context, context.getString(R.string.Delete_file_success), Toast.LENGTH_SHORT).show()
                EventBus.getDefault().post(DeleteEvent(position))
                dismiss()
            } catch (e: Exception) {
                Toast.makeText(context, context.getString(R.string.delete_failes), Toast.LENGTH_SHORT).show()
            }
        }
    }

}