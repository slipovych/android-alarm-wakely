package com.wakely.wakely.screens.alarm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.wakely.wakely.App;
import com.wakely.wakely.R;
import com.wakely.wakely.model.Event;
import com.wakely.wakely.qrscanner.model.Qr;
import com.wakely.wakely.qrscanner.screens.QrActivity;
import com.wakely.wakely.qrscanner.screens.QrCreatingActivity;
import com.wakely.wakely.screens.main.MainActivity;
import com.wakely.wakely.services.AlarmService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * "Add/Edit alarm" screen.
 *
 * <p>Lets user pick time (MaterialTimePicker), optionally choose a mission QR, then schedules the
 * alarm by starting {@link AlarmService} and persists an {@link Event} in Room.</p>
 */
public class AlarmRedactor extends AppCompatActivity {


    private static final String EXTRA_EVENT = "AddAlarmActivity.EXTRA_EVENT";
    private static final int REQ_PICK_QR = 1001;

    Event event;

    private Button selectTimeButton;
    private ImageButton setAlarmButton;


    private SwitchMaterial missionSwitch;
    private TextView selectedTime;

    View mission; // plain button view

    private Intent alarmServiceIntent;

    private String time = "";
    private Integer selectedHour = null;
    private Integer selectedMinute = null;
    private Integer selectedQrId = null;
    private Calendar selectedCalendar = null;

    public static boolean MISSION_USAGE = false;



    public static void start(Activity caller, Event event) {
        Intent intent = new Intent(caller, AlarmRedactor.class);
        if (event != null) {
            intent.putExtra(EXTRA_EVENT, event);
        }
        caller.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_alarm_activity);

        // Fragment manager for working with fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        // SimpleDateFormat for working with time in string from TimePicker
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());



        // TextView for selected time. Default time is considered "selected" immediately.
        selectedTime = findViewById(R.id.selected_time);
        selectedCalendar = Calendar.getInstance();
        selectedCalendar.set(Calendar.SECOND, 0);
        selectedCalendar.set(Calendar.MILLISECOND, 0);
        ensureFuture(selectedCalendar);
        selectedHour = selectedCalendar.get(Calendar.HOUR_OF_DAY);
        selectedMinute = selectedCalendar.get(Calendar.MINUTE);
        time = sdf.format(selectedCalendar.getTime());
        selectedTime.setText(time);

        alarmServiceIntent = AlarmService.createScheduleIntent(
                this,
                /* eventId */ 0, // will be replaced after inserting the Event and getting uid
                selectedHour,
                selectedMinute,
                selectedCalendar
        );




        // Selecting the alarm time
        selectTimeButton = findViewById(R.id.select_time_button);
        selectTimeButton.setOnClickListener(view -> {
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(12)
                    .setMinute(0)
                    .build();

            timePicker.addOnPositiveButtonClickListener(view1 -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                calendar.set(Calendar.MINUTE, timePicker.getMinute());
                ensureFuture(calendar);

                selectedHour = timePicker.getHour();
                selectedMinute = timePicker.getMinute();
                selectedCalendar = calendar;
                time = sdf.format(calendar.getTime());

                alarmServiceIntent = AlarmService.createScheduleIntent(
                        AlarmRedactor.this,
                        /* eventId */ 0, // will be replaced on save
                        timePicker.getHour(),
                        timePicker.getMinute(),
                        calendar
                );

                // Setting time in string on TextView
                selectedTime.setText(time);

            });
            timePicker.show(getSupportFragmentManager(), "tag_picker");
        });

        mission = findViewById(R.id.mission_view);
        mission.setOnClickListener(view -> {
            startActivityForResult(new Intent(this, QrActivity.class), REQ_PICK_QR);
        });


        // Button confirms data and sets alarm
        setAlarmButton = findViewById(R.id.set_alarm);
        setAlarmButton.setOnClickListener(view -> {
            if (selectedHour == null || selectedMinute == null || selectedCalendar == null) {
                Toast.makeText(this, "Select time first", Toast.LENGTH_SHORT).show();
                return;
            }

            // Re-check at the moment of saving; user may have left the screen open.
            ensureFuture(selectedCalendar);

            Event event = new Event();
            event.time = time;
            event.scheduled = true;
            if (!MISSION_USAGE) {
                event.mission = null;
            }
            if (selectedQrId != null) {
                event.relatedQrId = selectedQrId;
            }

            long newId = App.getInstance().getEventDao().insertAll(event);
            event.uid = (int) newId;

            // Schedule after we have a stable event id for unique PendingIntent identity.
            Intent scheduleIntent = AlarmService.createScheduleIntent(
                    this,
                    event.uid,
                    selectedHour,
                    selectedMinute,
                    selectedCalendar
            );
            startForegroundService(scheduleIntent);


            Toast.makeText(this, " Alarm set on " + selectedTime.getText(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
        });



    }

    private static void ensureFuture(Calendar calendar) {
        if (calendar == null) return;
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_PICK_QR && resultCode == RESULT_OK && data != null) {
            int qrId = data.getIntExtra(QrActivity.EXTRA_SELECTED_QR_ID, -1);
            if (qrId != -1) {
                selectedQrId = qrId;
                MISSION_USAGE = true;
                Toast.makeText(this, "Mission QR selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
