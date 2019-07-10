package com.github.naz013.tasker.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.github.naz013.tasker.R
import com.github.naz013.tasker.SplashScreenActivity
import com.github.naz013.tasker.data.TaskGroup
import com.github.naz013.tasker.services.ActionsReceiver

class Notifier(val context: Context) {

    companion object {
        const val CHANNEL_GROUP = "group.channel"
    }

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            manager?.createNotificationChannel(createChannel(context))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(context: Context): NotificationChannel {
        val name = context.getString(R.string.group_channel)
        val descr = context.getString(R.string.default_group_notifications)
        val importance = NotificationManager.IMPORTANCE_LOW
        val mChannel = NotificationChannel(CHANNEL_GROUP, name, importance)
        mChannel.description = descr
        mChannel.setShowBadge(true)
        mChannel.enableVibration(false)
        mChannel.enableLights(false)
        return mChannel
    }

    fun hideNotification(group: TaskGroup) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        manager?.cancel(group.id)
    }

    fun showNotification(group: TaskGroup) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        manager?.cancel(group.id)
        val remoteViews = RemoteViews(context.packageName, R.layout.view_notification)
        val builder = NotificationCompat.Builder(context, CHANNEL_GROUP)
        builder.setAutoCancel(false)
        builder.setOngoing(true)
        builder.setContentTitle(group.name)
        builder.setSmallIcon(R.drawable.ic_app_icon_white)
        builder.priority = NotificationCompat.PRIORITY_MIN
        remoteViews.setTextViewText(R.id.titleView, group.name)
        remoteViews.setImageViewResource(R.id.bellIcon, R.drawable.ic_alarm_black)
        remoteViews.setImageViewResource(R.id.closeIcon, R.drawable.ic_cancel)
        val cancelIntent = Intent(context, ActionsReceiver::class.java)
                .setAction(ActionsReceiver.ACTION_CANCEL_NOTIFICATION)
                .putExtra(ActionsReceiver.ARG_ID, group.id)
        val cancelPendingIntent = PendingIntent.getBroadcast(context, group.id, cancelIntent, 0)
        remoteViews.setOnClickPendingIntent(R.id.closeIcon, cancelPendingIntent)
        var list = group.tasks
        val important = Prefs.getInstance(context).getImportant()
        val importantIds = Prefs.getInstance(context).getStringList(Prefs.IMPORTANT_FIRST_IDS)
        if (important == Prefs.ENABLED || (important == Prefs.CUSTOM && importantIds.contains(group.id.toString()))) {
            list = list.sortedByDescending { it.important }.toMutableList()
        }
        list = list.sortedByDescending { it.dt }.sortedBy { it.done }.toMutableList()
        if (list.isNotEmpty()) {
            list.forEach {
                val rV = RemoteViews(context.packageName, R.layout.item_task_notification)
                rV.setTextViewText(R.id.summaryView, it.summary)
                if (it.done) rV.setImageViewResource(R.id.statusView, R.drawable.ic_status_check_white)
                else rV.setImageViewResource(R.id.statusView, R.drawable.ic_status_non_check_white)
                if (it.important) rV.setImageViewResource(R.id.favouriteView, R.drawable.ic_favourite_on_white)
                else rV.setImageViewResource(R.id.favouriteView, R.drawable.ic_favourite_off_white)
                remoteViews.addView(R.id.containerView, rV)
            }
        } else {
            val rV = RemoteViews(context.packageName, R.layout.item_empty_tasks)
            remoteViews.addView(R.id.containerView, rV)
        }
        remoteViews.setInt(R.id.notificationBg, "setBackgroundColor", Color.parseColor(group.color))
        builder.setCustomBigContentView(remoteViews)
        val intent = Intent(context, SplashScreenActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, group.id, intent, 0)
        builder.setContentIntent(pendingIntent)
        manager?.notify(group.id, builder.build())
    }
}