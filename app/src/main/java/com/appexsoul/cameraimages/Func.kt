package com.appexsoul.cameraimages

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.ActionBar
import java.util.Objects

object Func {

    private var dialog: Dialog? = null

    // loading dialog
    fun loadingDialog(show: Boolean, context: Context?) {
        if (show) {
            dialog = Dialog(context!!)
            dialog!!.setContentView(R.layout.loading_dialog)
            dialog!!.setCancelable(false)
            Objects.requireNonNull(dialog!!.window)!!.setBackgroundDrawable(
                ColorDrawable(
                    Color.TRANSPARENT
                )
            )
            dialog!!.show()
            dialog!!.window!!
                .setLayout(
                    ActionBar.LayoutParams.WRAP_CONTENT,
                    ActionBar.LayoutParams.WRAP_CONTENT
                )
        } else {
            dialog?.dismiss()
        }
    }

}
