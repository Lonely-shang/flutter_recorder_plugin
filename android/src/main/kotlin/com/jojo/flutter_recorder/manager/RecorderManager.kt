package com.jojo.flutter_recorder.manager

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.jojo.flutter_recorder.FlutterRecorderPlugin
import com.jojo.flutter_recorder.R
import java.io.File
import java.io.IOException


/**
 * @calssName RecorderManager
 * @author Miliky
 * @date 2023/12/29 16:01
 * @description [爱已随风起，风止意难平。]
 */
@Suppress("DEPRECATION")
class RecorderManager : Service(){

    companion object {
        const val ACTION_START_RECORDING = "com.example.app.START_RECORDING"
        const val ACTION_STOP_RECORDING = "com.example.app.STOP_RECORDING"
    }

    private lateinit var powerManager: PowerManager
    private lateinit var wakeLock: PowerManager.WakeLock
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var recordingNotification: Notification
    private val CHANNEL_ID = "recording_channel"
    private val NOTIFICATION_ID = 1

    override fun onCreate() {
        super.onCreate()
        powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp:WakeLockTag")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        wakeLock.acquire()

        // 创建Notification并将Service设置为前台服务
//        createRecordingNotification()
        startForeground(NOTIFICATION_ID, recordingNotification)

        // 执行你的后台任务

        return START_STICKY
    }

    @Suppress("DEPRECATION")
    fun startRecorder(){
        wakeLock.acquire()

//        mediaRecorder = MediaRecorder()
//        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
//        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
//        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
//
//        val outputFile = getOutputFile()
//        mediaRecorder.setOutputFile(outputFile.absolutePath)
//
//        try {
//            mediaRecorder.prepare()
//            mediaRecorder.start()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }

//        createRecordingNotification()
        startForeground(NOTIFICATION_ID, recordingNotification)
    }

    private fun stopRecording() {
        if (::mediaRecorder.isInitialized) {
            mediaRecorder.stop()
            mediaRecorder.release()
        }

        if (wakeLock.isHeld) {
            wakeLock.release()
        }

        stopForeground(true)
        stopSelf()
    }

    private fun getOutputFile(): File {
        // 生成录音文件的路径和文件名
        val directory = File(getExternalFilesDir(null)?.absolutePath)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val rootDirectory = File(Environment.getExternalStorageDirectory(), "MyAppFolder")
        if (!rootDirectory.exists()) {
            rootDirectory.mkdir()
        }
        return File(rootDirectory, "recording_${System.currentTimeMillis()}.mp3")
    }

//    private fun createRecordingNotification() {
//        val notificationIntent = Intent(this, FlutterRecorderPlugin::class.java)
//        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
//
//        notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setContentTitle("Recording")
//            .setContentText("Recording in progress")
//            .setContentIntent(pendingIntent)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setOngoing(true)
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                CHANNEL_ID,
//                "Recording Channel",
//                NotificationManager.IMPORTANCE_LOW
//            )
//            val notificationManager =
//                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        recordingNotification = notificationBuilder.build()
//    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}