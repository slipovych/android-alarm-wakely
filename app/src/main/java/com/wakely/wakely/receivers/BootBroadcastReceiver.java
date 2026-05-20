package com.wakely.wakely.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.wakely.wakely.services.AlarmService;

/**
 * BOOT_COMPLETED receiver.
 *
 * <p>Legacy note: rescheduling alarms on boot is not implemented because the app does not persist
 * enough metadata (exact timestamp / enabled state). This receiver currently only logs.</p>
 */
public class BootBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = "";
    @Override
    public void onReceive(Context context, Intent intent) {
        // Previously this started AlarmService without scheduling extras, which caused crashes.
        // Proper re-scheduling should read saved alarms from DB and schedule them, but this app
        // currently doesn't persist enough scheduling metadata. Keep receiver as a no-op aside from logging.
        Log.d(TAG, "onReceive: boot");
    }
}
