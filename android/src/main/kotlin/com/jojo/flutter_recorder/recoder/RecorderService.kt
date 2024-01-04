package com.jojo.flutter_recorder.recoder

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.*
import androidx.core.app.NotificationCompat
import java.io.File
import java.io.IOException


/**
 * @calssName RecorderService
 * @author Miliky
 * @date 2024/1/2 9:50
 * @description [爱已随风起，风止意难平。]
 */
@Suppress("DEPRECATION")
class RecorderService : Service(), MediaRecorder.OnErrorListener {

    private lateinit var powerManager: PowerManager
    private lateinit var wakeLock: PowerManager.WakeLock
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var recordingNotification: Notification
    private val CHANNEL_ID = "recording_channel"
    private val NOTIFICATION_ID = 1

    private lateinit var recorder: MediaRecorder
    private var isRecording : Boolean = false
    private var thread: Thread? = null

    private var handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
       @Override
         override fun handleMessage(msg: Message) {
              super.handleMessage(msg)
              if (isRecording) {
                val time = recorder.maxAmplitude / 100
                  var db: Double = 0.0
                  if (time > 1) {
                      db = 20 * Math.log10(time.toDouble())
                  }
                  if (db < 0) {
                      db = 0.0
                      recorder.prepare()
                  }
                  notificationBuilder.setContentText("Recording in progress: ${db.toInt()} dB")
                  notificationBuilder.setSmallIcon(1)
                    val notificationManager =
                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                  notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
                  println(db)
                  return
              }
         }
    }


    override fun onCreate() {
        super.onCreate()
        powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp:WakeLockTag")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startRecorder()

       return START_STICKY
    }

    fun startRecorder(){


        recorder = MediaRecorder()
        isRecording = true
        recorder.reset()
        setRecorder()

        wakeLock.acquire()
        createRecordingNotification()
        startForeground(NOTIFICATION_ID, recordingNotification)

    }

    private fun setRecorder() {
        var file1 = File(Environment.getExternalStorageDirectory().path + "/jojoRecorder/record.amr")
        file1.createNewFile()
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB)
        recorder.setOutputFile(file1.path)
      try {
          recorder.prepare()
          recorder.start()
          thread = Thread(Runnable {
              kotlin.run {
                  while (isRecording) {
                      handler.sendEmptyMessage(0)
                      try {
                          Thread.sleep(1000)
                      } catch (e: InterruptedException) {
                          e.printStackTrace()
                      }
                  }
              }
          })

          thread!!.start()
      } catch (e: IOException) {
          e.printStackTrace()
      }
    }

    fun stopRecorder(){
        if (wakeLock.isHeld) {
            wakeLock.release()
        }
        if (isRecording) {
            recorder.stop()
            recorder.release()
            isRecording = false
            thread!!.interrupt()
            stopForeground(true)
            stopSelf()
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
        val notificationIntent = Intent(this, MediaRecorder::class.java)
////        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT)
        }
        notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("睿信")
            .setContentText("Recording in progress")
            .setSmallIcon(1)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Recording Channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        recordingNotification = notificationBuilder.build()
    }

    fun hideNotification() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // 删除通道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (manager.getNotificationChannel(CHANNEL_ID) != null) {
                manager.cancel(NOTIFICATION_ID)
                manager.deleteNotificationChannel(CHANNEL_ID)
            }
        }
    }

    override fun onError(mr: MediaRecorder?, what: Int, extra: Int) {
        println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~error~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
//        stopRecorder()
    }
}