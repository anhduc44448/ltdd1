package com.example.thicki.ui.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Nhắc nhở lịch khám"
        val message = intent.getStringExtra("message") ?: "Bạn có lịch hẹn khám trong 24h tới."
        
        val notificationHelper = NotificationHelper(context)
        notificationHelper.showNotification(title, message)
    }
}
