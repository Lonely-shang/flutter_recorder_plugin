package com.jojo.flutter_recorder.recoder

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.*
import androidx.core.app.NotificationCompat
import com.jojo.flutter_recorder.FlutterRecorderPlugin
import java.io.File
import java.io.IOException


/**
 * @calssName RecorderService
 * @author Miliky
 * @date 2024/1/2 9:50
 * @description [爱已随风起，风止意难平。]
 */
@Suppress("DEPRECATION")
class RecorderService : Service() {
    private lateinit var powerManager: PowerManager
    private lateinit var wakeLock: PowerManager.WakeLock
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var recordingNotification: Notification
    private val CHANNEL_ID = "recording_channel"
    private val NOTIFICATION_ID = 1

    private lateinit var recorder: MediaRecorder
    private var isRecording : Boolean = false

    override fun onCreate() {
        super.onCreate()
        powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp:WakeLockTag")
    }

    open fun startRecorder(){
        wakeLock.acquire()
        createRecordingNotification()
        startForeground(NOTIFICATION_ID, recordingNotification)

        recorder = MediaRecorder()
        isRecording = true
        recorder.reset()
        setRecorder()
    }

    private fun setRecorder() {
        var file = File("/storage/emulated/0/jojoRecorder/record.amr")
        var file1 = File(Environment.getExternalStorageDirectory().path + "/jojoRecorder/record.amr")
        file1.createNewFile()
        if (file.exists()) {
            file.delete()
        } else {
            file.createNewFile()
        }
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB)
        recorder.setOutputFile(file1.path)
      try {
          recorder.prepare()
          recorder.start()
      } catch (e: IOException) {
          e.printStackTrace()
      }
    }

    fun stopRecorder(){
        if (isRecording) {
            recorder.stop()
            recorder.release()
            isRecording = false
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return  RecorderBinder()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    inner class RecorderBinder : Binder() {
        fun getService(): RecorderService = this@RecorderService
    }

    private fun createRecordingNotification() {
        val notificationIntent = Intent(this, FlutterRecorderPlugin::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Recording")
            .setContentText("Recording in progress")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Recording Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        recordingNotification = notificationBuilder.build()
    }
}