package com.appexsoul.cameraimages

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.ActionBar

object Func {

    private var dialog: Dialog? = null

    // loading dialog
    fun loadingDialog(show: Boolean, context: Context?) {
        if (show) {
            val ctx = context ?: return

            dialog = Dialog(ctx).apply {
                setContentView(R.layout.loading_dialog)
                setCancelable(false)
                window?.setBackgroundDrawable(
                    ColorDrawable(Color.TRANSPARENT)
                )
                show()
                window?.setLayout(
                    ActionBar.LayoutParams.WRAP_CONTENT,
                    ActionBar.LayoutParams.WRAP_CONTENT
                )
            }
        } else {
            dialog?.dismiss()
        }
    }

}
