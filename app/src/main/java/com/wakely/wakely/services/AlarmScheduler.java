package com.wakely.wakely.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.wakely.wakely.model.Event;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Non-UI alarm scheduling/canceling logic.
 *
 * <p>UI layers (Activities/ViewModels/Adapters) should call into this class instead of dealing
 * with {@link AlarmManager} / {@link PendingIntent} directly.</p>
 */
public final class AlarmScheduler {

    private AlarmScheduler() {
    }

    public static void schedule(Context context, Event event) {
        if (event == null || event.uid <= 0) {
            return;
        }

        int[] hm = parseHourMinute(event.time);
        if (hm == null) {
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.HOUR_OF_DAY, hm[0]);
        calendar.set(Calendar.MINUTE, hm[1]);

        // If the chosen time is already in the past today, schedule for tomorrow.
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        Intent alarmServiceIntent = AlarmService.createScheduleIntent(context, event.uid, hm[0], hm[1], calendar);
        context.startForegroundService(alarmServiceIntent);
    }

    public static void cancel(Context context, Event event) {
        if (event == null || event.uid <= 0) {
            return;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            return;
        }

        PendingIntent pi = AlarmService.createAlarmActionPendingIntent(context, event.uid);
        alarmManager.cancel(pi);
    }

    private static int[] parseHourMinute(String time) {
        if (time == null) {
            return null;
        }

        // Accept both "HH:mm" and legacy "HH : mm" variants by extracting digits.
        Pattern p = Pattern.compile("(\\d{1,2})");
        Matcher m = p.matcher(time);
        if (!m.find()) return null;
        int hour = Integer.parseInt(m.group(1));
        if (!m.find()) return null;
        int minute = Integer.parseInt(m.group(1));
        if (hour < 0 || hour > 23 || minute < 0 || minute > 59) return null;
        return new int[]{hour, minute};
    }
}
