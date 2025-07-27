package com.appexsoul.cameraimages

import android.telephony.SmsManager

object SMSHelper {
    fun sendCommand(phone: String, command: String) {
        val manager = SmsManager.getDefault()
        manager.sendTextMessage(phone, null, command, null, null)
    }
}
