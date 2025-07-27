package com.appexsoul.cameraimages

import android.telephony.SmsManager
import android.telephony.SubscriptionManager

object SMSHelper {
    fun sendCommand(phone: String, command: String) {
        val subscriptionId = SubscriptionManager.getDefaultSmsSubscriptionId()
        val manager = SmsManager.getSmsManagerForSubscriptionId(subscriptionId)
        manager.sendTextMessage(phone, null, command, null, null)
    }
}
