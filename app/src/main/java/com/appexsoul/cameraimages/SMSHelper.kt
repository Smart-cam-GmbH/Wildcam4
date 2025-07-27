package com.appexsoul.cameraimages

import android.telephony.SmsManager

object SMSHelper {
    fun sendCommand(phone: String, command: String) {
        val manager = SmsManager.getDefault()
        manager.sendTextMessage(phone, null, command, null, null)
    }

    /**
     * Normalize an SMS command entered by the user.
     * Whitespace is trimmed and the command is upper-cased.
     */
    fun parseCommand(raw: String): String {
        return raw.trim().uppercase()
    }
}
