package com.wakely.wakely.screens.alarm;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.wakely.wakely.R;
import com.wakely.wakely.screens.main.MainActivity;

/**
 * Screen shown when the scheduled alarm fires.
 *
 * <p>Plays the default alarm ringtone until the user turns it off.</p>
 */
public class AlarmActivity extends AppCompatActivity {

    Button stopAlarm;
    TextView timeString;
    Ringtone ringtone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        // Searching and setting device default alarm ringtone
        // as app ringtone
        Uri notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(this, notificationUri);
        if (ringtone == null) {
            notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            ringtone = RingtoneManager.getRingtone(this, notificationUri);
        } else
            ringtone.play();


        // Getting time info from AlarmService
        Bundle hourMinuteData = getIntent().getExtras();
        int hour = 0;
        int minute = 0;
        if (hourMinuteData != null) {
            try {
                hour = Integer.parseInt(hourMinuteData.getString("hourString", "0"));
                minute = Integer.parseInt(hourMinuteData.getString("minuteString", "0"));
            } catch (NumberFormatException ignored) {
                // keep defaults
            }
        }

        // Formatting time strings 1:5 -> 01:05
        String timeData = String.format("%02d : %02d", hour, minute);
        timeString = findViewById(R.id.time_string);
        timeString.setText(timeData);


        // Defining button and setting click listener
        // that is stopping ringtone, transfer user to MainActivity and destroying AlarmActivity
        stopAlarm = findViewById(R.id.alarm_stop);
        stopAlarm.setOnClickListener(v -> {
            ringtone.stop();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
