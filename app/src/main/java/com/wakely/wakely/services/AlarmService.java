package com.wakely.wakely.services;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.wakely.wakely.R;
import com.wakely.wakely.screens.alarm.AlarmActivity;
import com.wakely.wakely.screens.main.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Foreground {@link IntentService} that schedules an alarm via {@link AlarmManager}.
 *
 * <p>It expects scheduling extras ({@link #EXTRA_HOUR}, {@link #EXTRA_MINUTE}, {@link #EXTRA_CALENDAR}).
 * When started without them, it does nothing.</p>
 */
public class AlarmService extends IntentService {

    public static final String TAG = "";

    public static final String EXTRA_HOUR = "hour";
    public static final String EXTRA_MINUTE = "minute";
    public static final String EXTRA_CALENDAR = "calendar";
    public static final String EXTRA_EVENT_ID = "event_id";

    // Constructor
    public AlarmService() {
        super("alarmservice");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "SerOnCr");
    }


    // Main code
    @SuppressLint("ScheduleExactAlarm")
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());

        if (intent == null || intent.getExtras() == null) {
            // Service was started without the required scheduling data (e.g. after BOOT).
            // Nothing to do.
            return;
        }

        // Getting data from TimePicker
        Bundle args = intent.getExtras();

        int eventId = args.getInt(EXTRA_EVENT_ID, 0);
        if (eventId <= 0) {
            // Without a stable identifier, we cannot reliably cancel/update this alarm later.
            return;
        }

        // Setting calendar
        Calendar calendar = (Calendar) args.get(EXTRA_CALENDAR);
        if (calendar == null) {
            return;
        }
        // If an alarm is scheduled for a time that already passed today, many Android builds will
        // trigger it immediately. Normalize to the next future occurrence.
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // String values for sending to Alarm screen
        String hourString = String.valueOf(args.get(EXTRA_HOUR));
        String minuteString = String.valueOf(args.get(EXTRA_MINUTE));

        // Setting Alarm Manager
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), getAlarmInfoPendingIntent(eventId));
        alarmManager.setAlarmClock(alarmClockInfo, getAlarmActionPendingIntent(eventId, hourString, minuteString));

        // Notification for service
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        // Creating channel for notification
        String channelID;
        channelID = createNotificationChannel("my_service", "MyBackgroundService");

        Notification notification = new NotificationCompat.Builder(this, channelID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(2)
                .setContentTitle("Wakely")
                .setContentText("Alarm set on: " + sdf.format(calendar.getTime()))
                .setContentIntent(notificationPendingIntent).build();

        startForeground(7, notification);

    }




    private PendingIntent getAlarmInfoPendingIntent(int eventId) {
        Intent alarmInfoIntent = new Intent(this, MainActivity.class);
        alarmInfoIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        return PendingIntent.getActivity(this, eventId, alarmInfoIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private PendingIntent getAlarmActionPendingIntent(int eventId, String hourString, String minuteString) {
        Intent intent = new Intent(this, AlarmActivity.class);

        intent.putExtra("hourString", hourString);
        intent.putExtra("minuteString", minuteString);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        return PendingIntent.getActivity(this, eventId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    public static Intent createScheduleIntent(Context context, int eventId, int hour, int minute, Calendar calendar) {
        Intent i = new Intent(context, AlarmService.class);
        i.putExtra(EXTRA_EVENT_ID, eventId);
        i.putExtra(EXTRA_HOUR, hour);
        i.putExtra(EXTRA_MINUTE, minute);
        i.putExtra(EXTRA_CALENDAR, calendar);
        return i;
    }

    /**
     * PendingIntent used as the "alarm action" (what fires when the alarm triggers).
     * Must match the identity used by {@link #getAlarmActionPendingIntent(int, String, String)}.
     */
    public static PendingIntent createAlarmActionPendingIntent(Context context, int eventId) {
        Intent intent = new Intent(context, AlarmActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(
                context,
                eventId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelID, String channelName) {
        NotificationChannel notificationChannel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setLightColor(Color.BLUE);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        service.createNotificationChannel(notificationChannel);

        return channelID;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "SerOnDest");
    }
}
