package com.example.servicesactivity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class AudioService extends Service {
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    "boohoo",
                    "Media Playback Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            serviceChannel.setSound(null,null); // Disable sound for this channel

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
                Log.d("MediaService", "Notification channel '" + "boohoo" + "' created with importance DEFAULT.");
            } else {
                Log.e("MediaService", "NotificationManager is null, channel not created.");
            }
        }
    }
    public AudioService() {
    }
    MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.final_countdown);

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MediaService", "onCreate called");
        mediaPlayer = MediaPlayer.create(this, R.raw.final_countdown);
        mediaPlayer.setLooping(false);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d("MediaService", "onStartCommand called");
        createNotificationChannel();

        Notification notification = new NotificationCompat.Builder(this, "boohoo")
                .setContentTitle("Music Playing")
                .setContentText("Your song is playing")
                .setSmallIcon(android.R.drawable.ic_media_play)
                .build();

        startForeground(1, notification);
        if (intent != null) {
            String action = intent.getAction();
            if ("PLAY".equals(action)) {
                if (mediaPlayer != null) {
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                    }
                }
            } else if ("STOP".equals(action)) {
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                    mediaPlayer.seekTo(0); // rewind to beginning
                }
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MediaService", "onDestroy called");
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}