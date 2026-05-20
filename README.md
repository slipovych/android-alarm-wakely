# Wakely (legacy)

Android app prototype for scheduling one-off alarms, with an optional "mission" concept backed by QR codes.

This repo is a legacy codebase. Recent changes focused on making the existing behavior buildable and stable, and on wiring missing pieces of the intended UX (toggle, delete, per-event alarm identity).

**Language / stack**

- Language: Java (Android)
- UI: Activities + RecyclerView + Material Components
- Persistence: Room (SQLite)
- Alarm scheduling: `AlarmManager.setAlarmClock(...)` via a foreground `IntentService`
- QR scanning: `com.github.yuriy-budiyev:code-scanner`
- QR generation: `com.github.androidmads:QRGenerator` (+ `com.google.zxing:core`)

## What The App Can Do (Current)

1. Show a list of stored events (`Event`) from Room on the main screen.
2. Create a new event with a chosen time (default time is pre-selected when the screen opens).
3. Schedule a system alarm for that event and show a foreground notification while scheduling.
4. Trigger `AlarmActivity` when the alarm fires, play ringtone until user turns it off.
5. Enable/disable an event directly from the list via a switch (updates DB + schedules/cancels the system alarm).
6. Delete an event from the list (removes from Room; cancels the system alarm if it was enabled).
7. Scan QR codes via camera and save them; select a saved QR as a "mission" attached to an event (`relatedQrId`).

## UI / Screens

1. `MainActivity` (`activity_main.xml`)
   - RecyclerView of events (`MainAdapter`)
   - `+` button opens `AlarmRedactor`
   - Each row:
     - time
     - enable/disable switch
     - delete button

2. `AlarmRedactor` (`add_alarm_activity.xml`)
   - Shows current selected time (pre-selected by default)
   - "Select Time" opens a Material time picker; the TextView always reflects the last confirmed selection
   - "Mission" opens `QrActivity` to pick a QR
   - Save button persists an `Event`, then schedules the alarm for that `Event.uid`

3. `AlarmActivity` (`activity_alarm.xml`)
   - Shows alarm time
   - "TURN OFF" stops ringtone and returns to main screen

4. `QrActivity` (`activity_qr.xml`)
   - List of stored QR items
   - Done returns selected `qrId`
   - "Add QR" opens `QrCreatingActivity`

5. `QrCreatingActivity` (`activity_set_qr.xml`)
   - Embeds `QrScannerFragment` to scan a code
   - Saves a `Qr` (name + raw contents)

## Data Model / Database

Room database name: `app-database`.

Entities:

- `Event` (`com.wakely.wakely.model.Event`)
  - `uid` (PK, auto)
  - `time` (display string, currently used for scheduling too)
  - `mission` (legacy/unused)
  - `scheduled` (boolean: enabled/disabled state)
  - `relatedQrId` (optional FK-like reference to `Qr.qrId`)

- `Qr` (`com.wakely.wakely.qrscanner.model.Qr`)
  - `qrId` (PK, auto)
  - `qrName`
  - `qrCode` (raw scanned text)

DAOs:

- `EventDao` (`com.wakely.wakely.data.EventDao`)
  - `getAllLiveData()` drives the main list
  - `insertAll(Event)` returns inserted row id (used to get `Event.uid` for alarm identity)
  - `update(Event)` / `delete(Event)`

- `QrDao` (`com.wakely.wakely.data.QrDao`)

There is no prepopulated data in the repo. Data exists only on a device/emulator after you run the app.

## Controllers / Services

UI controllers:

- `MainActivity` + `MainViewModel` + `MainAdapter` (events list)
- `AlarmRedactor` (create event + schedule)
- `AlarmActivity` (alarm ringing UI)
- QR flows: `QrActivity`, `QrCreatingActivity`, `QrScannerFragment`

Non-UI alarm logic:

- `AlarmScheduler` (`com.wakely.wakely.services.AlarmScheduler`)
  - Schedules/cancels per-event alarms based on `Event.uid`
  - UI calls into this class; adapters stay UI-only

Scheduling service:

- `AlarmService` (`com.wakely.wakely.services.AlarmService`)
  - Foreground `IntentService`
  - Receives `EXTRA_EVENT_ID`, `EXTRA_HOUR`, `EXTRA_MINUTE`, `EXTRA_CALENDAR`
  - Calls `AlarmManager.setAlarmClock(...)`
  - Uses `eventId` as `PendingIntent` requestCode so alarms can be canceled per event
  - Normalizes schedule time: if calendar time is in the past, it shifts to the next day to avoid immediate firing

## Pipelines

**Create event**

1. User opens `AlarmRedactor`.
2. Time is pre-selected; user may change it via the time picker.
3. On save: insert `Event` into Room, obtain `uid`.
4. Start `AlarmService` with `EXTRA_EVENT_ID = uid` and the selected time.

**Toggle enable/disable from list**

1. User flips the switch in a RecyclerView row.
2. `MainAdapter` calls a callback to `MainActivity`.
3. `MainActivity` updates `Event.scheduled` in Room.
4. `MainActivity` calls `AlarmScheduler.schedule(...)` or `AlarmScheduler.cancel(...)`.

**Delete event**

1. User taps delete button in a row.
2. `MainAdapter` calls a callback to `MainActivity`.
3. `MainActivity` cancels alarm if needed, then deletes the `Event` from Room.

## Build Notes

This project uses Android Gradle Plugin `7.4.2` and Gradle `7.5` (wrapper).

Practical build requirements:

- Use JDK 11 for this Gradle/AGP combination.
- The project uses JitPack for some dependencies.

Example CLI build (macOS):

```bash
env JAVA_HOME=/Users/romanslipovych/Library/Java/JavaVirtualMachines/jbr_dcevm-11.0.16/Contents/Home \
    ./gradlew :app:assembleDebug
```

## Known Limitations (Current Behavior)

- `Event.time` is stored as a string; there is no persisted date/time model (next occurrence is inferred).
- No repeating alarms / days-of-week scheduling.
- No “edit existing event” flow (creating new works; editing/rescheduling would need cancel+schedule by uid).
- BOOT receiver does not reschedule alarms after reboot (would require persisting an exact next trigger time).
- DB access is configured with `allowMainThreadQueries()` (fine for prototype, not for production).
- Alarm scheduling uses a foreground `IntentService` (legacy approach; modern Android typically prefers `WorkManager`/`ForegroundService` patterns depending on use case).

## TODO (Product / Tech)

- Implement editing an existing `Event` (including rescheduling with the same `uid`).
- Persist structured scheduling fields in `Event`:
  - hour/minute as ints, timezone, nextTriggerAt millis
  - optional repeat rules
- Reschedule enabled alarms on device reboot (BOOT receiver).
- Move DB + scheduling operations off the main thread.
- Improve alarm cancellation/scheduling UX:
  - confirmation/snackbar undo on delete
  - error handling for invalid time strings
- Permissions and UX hardening for QR scanning (camera permission flow, empty/error states).
