package com.appexsoul.cameraimages

import android.content.Context
import android.telephony.SmsManager
import android.telephony.SubscriptionManager

object SMSHelper {
    fun sendCommand(context: Context, phone: String, command: String) {
        val subscriptionId = SubscriptionManager.getDefaultSmsSubscriptionId()
        val manager = SmsManager.getSmsManagerForSubscriptionId(subscriptionId)
        manager.sendTextMessage(phone, null, command, null, null)
    }
}
