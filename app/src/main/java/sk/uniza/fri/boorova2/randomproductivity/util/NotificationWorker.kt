package sk.uniza.fri.boorova2.randomproductivity.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import sk.uniza.fri.boorova2.randomproductivity.R

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val taskName = inputData.getString("taskName") ?: return Result.failure()
        val taskId = inputData.getLong("taskId", -1)
        if (taskId == -1L) return Result.failure()

        showNotification(taskName, taskId)
        return Result.success()
    }

    private fun showNotification(taskName: String, taskId: Long) {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "deadline_notifications"

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, applicationContext.getString(R.string.deadline_notifications), NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(applicationContext.getString(R.string.task_deadline_reminder_title))
            .setContentText(applicationContext.getString(R.string.task_deadline_reminder_text, taskName))
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        manager.notify(taskId.toInt(), notification)
    }
}